import math
import os
import re
import requests
import urllib3


urllib3.disable_warnings()

from bs4 import BeautifulSoup, Tag
from datetime import datetime
from typing import Any

from app.core import Bot
from app.db import close_db, init_db
from app.schemas import Licitacao_Pydantic, LicitacaoIn_Pydantic, LicitacaoItemIn_Pydantic
from app.services import LicitacaoService, LicitacaoItemService
from app.utils import normalize_string_keeping_symbol, async_enumerate


class LicitacaoBot(Bot):

    def __init__(self):
        super().__init__()

    async def _setup(self):
        try:
            await init_db(os.getenv("DB_HOST"))
            self._logger.debug("Estabelecendo conexão com banco de dados...")
        except Exception as err:
            self._logger.error(f"Erro ao conectar ao banco de dados: {str(err)}", exc_info=True)
            raise err
        else:
            self._logger.debug(f"Conexão com banco de dados estabelecida com sucesso!")

    async def _teardown(self):
        try:
            self._logger.debug("Fechando conexões com banco de dados...")
            await close_db()
            self._logger.debug(f"Conexão com banco de dados fechada com sucesso!")
        except Exception as err:
            self._logger.error(f"Erro ao fechar conexão ao banco de dados: {str(err)}", exc_info=True)
            raise err
        finally:
            pass

    async def _start(self):
        try:
            licitacaoService = LicitacaoService()
            licitacaoItemService = LicitacaoItemService()
            total_pages = await self._get_total_pages()
            self._logger.debug(f"Obtendo dados do banco de dados...")
            licitacoes_cadastradas: list[dict[str, Any]] = await licitacaoService.get_all_uasg_and_numero_pregao()
            self._logger.debug(f"Quantidade de licitações cadastradas no banco de dados: {len(licitacoes_cadastradas)}")
            for page_num in range(1, total_pages + 1):
                self._logger.info(f"Acessando página {page_num} de {total_pages}")
                page = await self._get_page(page_num)
                licitacoes = self._get_licitacoes(page)
                async for index, licitacao in async_enumerate(licitacoes, 1):
                    UASG = licitacao.uasg
                    NUMERO_PREGAO = licitacao.numero_pregao
                    licitacao_cadastrada = any(item["uasg"] == UASG and item["numero_pregao"] == NUMERO_PREGAO for item in licitacoes_cadastradas)
                    if not licitacao_cadastrada:
                        self._logger.info(f"[{index}] Licitação nova encontrada na página {page_num}: UASG: {UASG} - Nº: {NUMERO_PREGAO}")
                        licitacao_nova = await licitacaoService.create(licitacao)
                        itens_da_licitacao = await self._get_itens_licitacao(licitacao_nova)
                        await licitacaoItemService.bulk_create(itens_da_licitacao)
                        licitacoes_cadastradas.append({"uasg": licitacao_nova.uasg, "numero_pregao": licitacao_nova.numero_pregao})
                    else:
                        self._logger.info(f"Não há novas licitações. Interrompendo iteração...")
                        return  # última licitação encontrada, parar o loop
        except Exception as err:
            self._logger.error(f"Ocorreu um erro durante a execução do bot: {err}", exc_info=True)
            raise err

    async def _get_total_pages(self):
        try:
            soup = await self._get_page(1)
            text = soup.select_one("center").get_text()
            pattern = re.compile(r"de (\d+)")
            matcher = pattern.search(text)
            total = int(matcher.group(1))
            self._logger.info(f"Total de licitações: {total}")
            if not matcher:
                raise RuntimeError("Paginação não encontrada")
            return math.ceil(total / 20)
        except Exception as err:
            self._logger.error(f"Erro ao obter total de licitações: {err}", exc_info=True)
            raise err

    async def _get_licitacoes(self, soup: BeautifulSoup):
        service = LicitacaoService()
        elements = soup.select("form tr.tex3 td")
        for element in elements:
            licitacao = await self._get_licitacao(element)
            yield licitacao

    async def _get_licitacao(self, element: Tag):
        properties = {}
        lines = await self.__handle_text(element)
        lines.pop()  # Remove a última linha
        while len(lines) > 0:
            line = lines.pop(0)
            pattern = re.compile(r"\s(\d+/\d{4})\s", re.IGNORECASE)
            matcher = pattern.search(line)
            try:
                if line.startswith("Código da UASG:"):
                    assert properties.get("uasg") is None
                    uasg = int(line.split("Código da UASG:", 1)[1].strip())
                    properties["uasg"] = uasg
                elif matcher and properties.get("numero_pregao") is None:
                    assert properties.get("numero_pregao") is None
                    numero_pregao = int(matcher.group(1).replace("/", ""))
                    properties["numero_pregao"] = numero_pregao
                elif line.startswith("Objeto:"):
                    assert properties.get("objeto") is None
                    objeto = line.split("Objeto:", 1)[1].strip()
                    properties["objeto"] = objeto[8:]
                elif line.startswith("Endereço:"):
                    assert properties.get("endereco") is None
                    endereco = line.split("Endereço:", 1)[1].strip()
                    properties["endereco"] = endereco
                elif line.startswith("Edital a partir de:"):
                    assert properties.get("horario_edital") is None
                    properties["horario_edital"] = line.split("Edital a partir de:", 1)[1].strip()
                elif line.startswith("Telefone:"):
                    assert properties.get("telefone") is None
                    telefone = line.split("Telefone:", 1)[1].strip()
                    properties["telefone"] = telefone if len(telefone) > 0 else None
                elif line.startswith("Fax:"):
                    assert properties.get("fax") is None
                    fax = line.split("Fax:", 1)[1].strip()
                    properties["fax"] = fax if len(fax) > 0 else None
                elif line.startswith("Entrega da Proposta:"):
                    assert properties.get("entrega_da_proposta") is None
                    entrega_da_proposta = line.split("Entrega da Proposta:", 1)[1].strip()
                    pattern = re.compile(r"(\d{2}/\d{2}/\d{4}).*(\d{2}:\d{2})")
                    matcher = pattern.search(entrega_da_proposta)
                    entrega_da_proposta = " ".join([matcher.group(1), matcher.group(2)])
                    entrega_da_proposta = datetime.strptime(entrega_da_proposta, "%d/%m/%Y %H:%M")
                    properties["entrega_da_proposta"] = entrega_da_proposta
                else:
                    if properties.get("unidade") is not None:
                        properties["unidade"] += f" | {line}"
                    else:
                        properties["unidade"] = line
            except Exception as err:
                self._logger.error(f"Erro durante a coleta de informações da licitação: {err}")
                raise err
        licitacao = LicitacaoIn_Pydantic(**properties)
        await self.__validate_licitacao(licitacao)
        return licitacao

    async def _get_itens_licitacao(self, licitacao: Licitacao_Pydantic):  # type: ignore
        itens: list[LicitacaoItemIn_Pydantic] = []  # type: ignore
        try:
            soup, modalidade = await self._get_item_page(licitacao, page=1)
            SELECTOR = "td.tex3 table:nth-child(2) td:nth-child(2)"
            elements = soup.select(SELECTOR)
            _quantidade_de_itens = soup.select_one("tr.tex3 td span.tex3")
            _quantidade_de_paginas = soup.select_one("tr.tex3 td:nth-child(2)")
            pagina_atual = 1
            TOTAL_DE_PAGINAS = 1
            if _quantidade_de_itens is not None:
                _quantidade_de_itens = int(_quantidade_de_itens.get_text().split(":")[1].strip())
            if _quantidade_de_paginas is not None:
                _quantidade_de_paginas = int(_quantidade_de_paginas.get_text().split("de")[1].strip())
                TOTAL_DE_PAGINAS = _quantidade_de_paginas
            self._logger.debug(f"Obtendo itens da licitação | UASG {licitacao.uasg} - Nº: {licitacao.numero_pregao} (modalidade: {modalidade})")
            total_de_itens = _quantidade_de_itens or (len(elements) - 1)
            if total_de_itens is None:
                raise Exception("Nenhum item de licitação foi encontrado")
            elif pagina_atual == TOTAL_DE_PAGINAS:
                elements.pop()
            self._logger.debug(f"Total de itens da licitação: {total_de_itens}")
            while pagina_atual <= TOTAL_DE_PAGINAS:
                self._logger.debug(f"Página {pagina_atual} de {TOTAL_DE_PAGINAS}")
                for element in elements:
                    properties = {}
                    lines = await self.__handle_text(element)
                    PATTERN = re.compile(r"\d+ - (.+)")
                    MATCHER = PATTERN.search(lines.pop(0))
                    assert MATCHER is not None
                    properties["nome"] = MATCHER.group(1).strip()
                    while len(lines) > 0:
                        line = lines.pop(0)
                        try:
                            if line.startswith("Tratamento Diferenciado:"):
                                assert properties.get("tratamento_diferenciado") is None
                                tratamento_diferenciado = line.split("Tratamento Diferenciado:", 1)[1].strip()
                                properties["tratamento_diferenciado"] = tratamento_diferenciado
                            elif line.startswith("Aplicabilidade Decreto 7174:"):
                                assert properties.get("aplicabilidade_decreto_7174") is None
                                aplicabilidade_decreto_7174 = line.split("Aplicabilidade Decreto 7174:", 1)[1].strip()
                                properties["aplicabilidade_decreto_7174"] = aplicabilidade_decreto_7174
                            elif line.startswith("Aplicabilidade Margem de Preferência:"):
                                assert properties.get("aplicabilidade_margem_de_preferencia") is None
                                aplicabilidade_margem_de_preferencia = line.split("Aplicabilidade Margem de Preferência:", 1)[1].strip()
                                properties["aplicabilidade_margem_de_preferencia"] = aplicabilidade_margem_de_preferencia
                            elif line.startswith("Quantidade:"):
                                assert properties.get("quantidade") is None
                                properties["quantidade"] = int(line.split("Quantidade:", 1)[1].strip())
                            elif line.startswith("Unidade de fornecimento:"):
                                assert properties.get("unidade_de_fornecimento") is None
                                unidade_de_fornecimento = line.split("Unidade de fornecimento:", 1)[1].strip()
                                properties["unidade_de_fornecimento"] = unidade_de_fornecimento
                            else:
                                assert properties.get("descricao") is None
                                properties["descricao"] = line.strip()
                        except Exception as err:
                            self._logger.error(f"Erro ao processar linha: {err}")
                            raise err
                    licitacao_item = LicitacaoItemIn_Pydantic(licitacao_id=licitacao.id, **properties)
                    [print(f"{field}: {licitacao_item.__getattribute__(field)}") for field in licitacao_item.model_fields_set]
                    await self.__validate_licitacao_item(licitacao_item)
                    itens.append(licitacao_item)
                    self._logger.info(f"Item de licitação coletado ({len(itens)} de {total_de_itens})")
                pagina_atual += 1
                if pagina_atual <= TOTAL_DE_PAGINAS:
                    soup, modalidade = await self._get_item_page(licitacao, page=pagina_atual, modalidade=modalidade)
                    elements = soup.select(SELECTOR)
            self._logger.info(f"Quantidade de itens da licitação: {len(elements)}")
            if len(itens) == 0:
                raise Exception("Licitação não encontrada em nenhuma das cinco modalidade")
            return itens
        except Exception as err:
            msg = f"Erro ao obter itens da licitação: UASG {licitacao.uasg} - Nº {licitacao.numero_pregao}: {err}"
            self._logger.error(msg, exc_info=True)
            raise err

    async def _get_page(self, page: int = 1):
        try:
            url = f"https://www.comprasnet.gov.br/ConsultaLicitacoes/ConsLicitacaoDia.asp?pagina={page}"
            response = requests.get(url, verify=False)
            if response.status_code != 200:
                raise RuntimeError(f"Status de resposta inesperado | Status Code: {response.status_code}")
            html = response.text
            soup = BeautifulSoup(html, "html.parser")
            return soup
        except Exception as err:
            msg = f"Erro ao acessar página {page}: {err}"
            self._logger.error(msg, exc_info=True)
            raise err

    async def _get_item_page(self, licitacao: Licitacao_Pydantic, page: int = 1, modalidade: int = None): # type: ignore
        try:
            BASE_URL = "https://www.comprasnet.gov.br/ConsultaLicitacoes/download/download_editais_detalhe.asp"
            uasg: int = licitacao.uasg
            num_pregao: int = licitacao.numero_pregao
            url = lambda modalidade: f"{BASE_URL}?coduasg={uasg}&numprp={num_pregao}&modprp={modalidade}"
            if modalidade is not None:
                assert modalidade in range(1, 6)
                response = requests.get(url(modalidade), verify=False)
                html = response.text
                if "Licitação não cadastrada." in html:
                    raise RuntimeError("Licitação não cadastrada para a modalidade escolhida.")
                return BeautifulSoup(html, "html.parser"), modalidade
            else:
                for mod in range(5, 0, -1):
                    response = requests.get(url(mod), verify=False)
                    html = response.text
                    if "Licitação não cadastrada." in html:
                        continue
                    return BeautifulSoup(html, "html.parser"), mod
        except Exception as err:
            msg = f"Erro ao acessar página de itens da licitação (uasg: {uasg}, num_pregao: {num_pregao}, modalidade: {modalidade}): {err}"
            self._logger.error(msg, exc_info=True)
            raise err

    async def __validate_licitacao(self, licitacao: Licitacao_Pydantic):  # type: ignore
        assert licitacao.uasg is not None
        assert licitacao.numero_pregao is not None

    async def __validate_licitacao_item(self, licitacao_item: LicitacaoItemIn_Pydantic):  # type: ignore
        assert licitacao_item.nome is not None
        assert licitacao_item.descricao is not None
        assert licitacao_item.tratamento_diferenciado is not None
        assert licitacao_item.aplicabilidade_decreto_7174 is not None
        assert licitacao_item.aplicabilidade_margem_de_preferencia is not None
        assert licitacao_item.unidade_de_fornecimento is not None
        assert licitacao_item.quantidade is not None

    async def __handle_text(self, td: Tag) -> list[str]:
        # 1. Substituir <br> por quebras de linha (\n)
        for br in td.find_all("br"):
            br.replace_with("\n")
        # 2. Substituir &nbsp; por espaços comuns
        text = td.get_text(separator=" ", strip=False)
        text = text.replace("\xa0", " ")  # \xa0 é a representação do &nbsp;
        # 3. Processar múltiplos espaços consecutivos
        text = re.sub(r" {2,}", " ", text)  # Reduz múltiplos espaços para um único
        # 4. Remover espaços antes de pontuação/quebras
        text = re.sub(r" \n", "\n", text)  # Remove espaço antes de quebras
        text = re.sub(r" ([.,:;])", r"\1", text)  # Remove espaço antes de pontuação
        return [normalize_string_keeping_symbol(line) for line in text.split("\n") if line.strip()]
