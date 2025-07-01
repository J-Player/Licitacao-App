from app.models import Licitacao

from app.schemas import Licitacao_Pydantic, LicitacaoIn_Pydantic


class LicitacaoRepository:
    _instance = None

    def __new__(cls, *args, **kwargs):
        if not cls._instance:
            cls._instance = super().__new__(cls, *args, **kwargs)
        return cls._instance

    async def create(self, licitacao_data: LicitacaoIn_Pydantic):  # type: ignore
        dados_criacao = licitacao_data.dict()
        licitacao_obj = await Licitacao.create(**dados_criacao)
        return await Licitacao_Pydantic.from_tortoise_orm(licitacao_obj)

    async def get_all_uasg_and_numero_pregao(self):
        return await Licitacao.all().values("uasg", "numero_pregao")
