package com.example.demo.services;

import com.example.demo.models.entities.Licitacao;
import com.example.demo.models.entities.LicitacaoItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScrapingService {

    private final LicitacaoService licitacaoService;
    private final LicitacaoItemService licitacaoItemService;

    public Flux<Licitacao> obterTodasAsLicitacoees() {
        final String baseUrl = "http://www.comprasnet.gov.br/ConsultaLicitacoes/ConsLicitacaoDia.asp";
        Map<String, Object> params = new HashMap<>();
        params.put("pagina", 1);
        return getPage(baseUrl, params)
                .flatMap(this::detectTotalPages)
                .flatMapMany(totalPages -> {
                    if (totalPages <= 0) return Flux.empty();
                    return Flux.range(1, 1)
                            .doOnNext(e -> log.info("Iniciando leitura da página {} de {}", e, totalPages))
                            .concatMap(page -> obterLicitacoes(page)
                                    .flatMapMany(Flux::fromIterable)
                                    .flatMap(licitacaoService::save)
                                    .doOnNext(l -> getItems(l)
                                            .doOnNext(x -> {
                                                x.setLicitacaoId(l.getId());
                                                licitacaoItemService.save(x);
                                            }))
                                    .onErrorContinue((ex, obj) ->
                                            log.error("Erro ao ler a página {}: ", page, ex)));
                });
    }

    public Mono<List<Licitacao>> obterLicitacoes(int page) {
        final String baseUrl = "http://www.comprasnet.gov.br/ConsultaLicitacoes/ConsLicitacaoDia.asp";
        Map<String, Object> params = new HashMap<>();
        params.put("pagina", page);
        return Mono.fromCallable(() -> getPage(baseUrl, params).map(this::parseItems))
                .flatMap(e -> e)
                .doOnNext(l -> log.info("Total de licitações encontradas: {}", l.size()))
                .subscribeOn(Schedulers.boundedElastic())
                .timeout(Duration.ofSeconds(15))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(500)));
    }

    private Mono<Integer> detectTotalPages(Document document) {
        return Mono.fromCallable(() -> {
            String text = document.selectXpath("//center").text();
            Pattern pattern = Pattern.compile("de (\\d+)");
            Matcher matcher = pattern.matcher(text);
            if (!matcher.find()) throw new RuntimeException("Paginação não encontrada");
            return (int) Math.ceil(Double.parseDouble(matcher.group(1)) / 20);
        });
    }

    private Mono<Document> getPage(String uri, Map<String, Object> params) {
        if (params != null) {
            var p = params.entrySet().stream()
                    .map(entry ->
                            String.format("%s=%s", entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining("&"));
            uri += "?" + p;
        }
        String finalUri = uri;
        return Mono.fromCallable(() -> Jsoup.connect(finalUri)
                .postDataCharset(StandardCharsets.ISO_8859_1.displayName())
                .get());
    }

    private List<Licitacao> parseItems(Document document) {
        Elements elements = document.selectXpath("//tr[@class='tex3']/td");
        List<Licitacao> licitacoes = new ArrayList<>();
        for (Element element : elements)
            licitacoes.add(getLicitacao(element));
        return licitacoes;
    }

    private Licitacao getLicitacao(Element element) {
        final String text = element.wholeText();
        final String[] arr = Arrays.stream(text.split("\n")).map(s -> s
                        .replace('\u00A0', ' ')
                        .trim()
                        .replaceAll("\\s+", " "))
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
        final List<String> lines = new ArrayList<>(Arrays.asList(arr));
        lines.removeLast(); //Ignora o último elemento
        Licitacao licitacao = Licitacao.builder().build();
        while (!lines.isEmpty()) {
            String linha = lines.removeFirst();
            Pattern pattern = Pattern.compile("\\s(\\d+/\\d+)\\s");
            Matcher matcher = pattern.matcher(linha);
            if (linha.startsWith("Código da UASG:")) {
                Integer uasg = Integer.parseInt(linha.substring("Código da UASG: ".length()));
                licitacao.setUasg(uasg);
            } else if (matcher.find() && licitacao.getNumeroPregao() == null) {
                linha = matcher.group(1).replaceAll("\\D", "");
                licitacao.setNumeroPregao(Integer.parseInt(linha));
            } else if (linha.startsWith("Objeto:")) {
                String objeto = linha.substring("Objeto: ".length() * 2).trim();
                licitacao.setObjeto(objeto);
            } else if (linha.startsWith("Edital a partir de:")) {
                String horarioEdital = linha.substring("Edital a partir de:".length()).trim();
                licitacao.setHorarioEdital(horarioEdital);
            } else if (linha.startsWith("Endereço:")) {
                String endereco = linha.substring("Endereço:".length()).trim();
                licitacao.setEndereco(endereco);
            } else if (linha.startsWith("Telefone:")) {
                String telefone = linha.substring("Telefone:".length()).replace("0xx", "").trim();
                licitacao.setTelefone(!telefone.isEmpty() ? telefone : null);
            } else if (linha.startsWith("Fax:")) {
                String fax = linha.substring("Fax:".length()).replace("0xx", "").trim();
                licitacao.setFax(fax.length() > 4 ? fax.replace("0xx", "") : null);
            } else if (linha.startsWith("Entrega da Proposta:")) {
                String proposta = linha.substring("Entrega da Proposta:".length()).trim();
                String[] strings = proposta.replace("Hs", "").split("\\s+");
                String dateString = String.join(" ", strings[0], strings[2]);
                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                final LocalDateTime entregaDaProposta = LocalDateTime.parse(dateString, formatter);
                licitacao.setEntregaDaProposta(entregaDaProposta);
            } else {
                if (licitacao.getUnidade() != null && !licitacao.getUnidade().isEmpty())
                    licitacao.setUnidade(licitacao.getUnidade() + " | " + linha);
                else licitacao.setUnidade(linha);
            }
        }
        return licitacao;
    }

    private Flux<LicitacaoItem> getItems(Licitacao licitacao) {
        String uri = "https://www.comprasnet.gov.br/ConsultaLicitacoes/download/download_editais_detalhe.asp";
        Map<String, Object> params = new HashMap<>();
        params.put("coduasg", licitacao.getUasg()); //CODIGO UASG
        params.put("numprp", licitacao.getNumeroPregao()); //NUMERO PREGAO
        params.put("modprp", 5); //MODALIDADE
        return Mono.fromCallable(() -> getPage(uri, params)
                        .map(document -> {
                            final String XPATH_CONTAINER = "/html/body/table[2]/tbody/tr[2]/td/table[2]/tbody/tr[3]/td[2]/table";
                            Element el = document.selectXpath(XPATH_CONTAINER).first();
                            return getLicitacaoItens(el);
                        }))
                .flatMap(mono -> mono)
                .flatMapMany(Flux::fromIterable);
    }

    private List<LicitacaoItem> getLicitacaoItens(Element element) {
        Elements elementItems = element.select("tr td:nth-child(2)");
        List<LicitacaoItem> licitacaoItems = new ArrayList<>(elementItems.size());
        for (Element elementItem : elementItems) {
            LicitacaoItem licitacaoItem = LicitacaoItem.builder().build();
            licitacaoItem.setNome(elementItem.selectFirst("span.tex3b").text());
            final String text = elementItem.selectFirst("span.tex3").wholeText();
            final String[] arr = Arrays.stream(text.split("\n")).map(s -> s
                            .replace('\u00A0', ' ')
                            .trim()
                            .replaceAll("\\s+", " "))
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);
            final List<String> lines = new ArrayList<>(Arrays.asList(arr));
            while (!lines.isEmpty()) {
                String linha = lines.removeFirst();
                if (linha.startsWith("Tratamento Diferenciado:")) {
                    String tratamentoDiferenciado = linha.substring("Tratamento Diferenciado:".length()).trim();
                    licitacaoItem.setTratamentoDiferenciado(tratamentoDiferenciado);
                } else if (linha.startsWith("Aplicabilidade Decreto 7174:")) {
                    String aplicabilidadeDecreto7174 = linha.substring("Aplicabilidade Decreto 7174:".length()).trim();
                    licitacaoItem.setAplicabilidadeDecreto7174(aplicabilidadeDecreto7174);
                } else if (linha.startsWith("Aplicabilidade Margem de Preferência:")) {
                    String aplicabilidadeMargemDePreferencia = linha.substring("Aplicabilidade Margem de Preferência:".length()).trim();
                    licitacaoItem.setAplicabilidadeMargemDePreferencia(aplicabilidadeMargemDePreferencia);
                } else if (linha.startsWith("Quantidade:")) {
                    Integer aplicabilidadeMargemDePreferencia = Integer.parseInt(linha.substring("Quantidade:".length()).trim());
                    licitacaoItem.setQuantidade(aplicabilidadeMargemDePreferencia);
                } else if (linha.startsWith("Unidade de fornecimento:")) {
                    String unidadeDeFornecimento = linha.substring("Unidade de fornecimento:".length()).trim();
                    licitacaoItem.setUnidadeDeFornecimento(unidadeDeFornecimento);
                } else {
                    if (licitacaoItem.getDescricao() != null && !licitacaoItem.getDescricao().isEmpty())
                        licitacaoItem.setDescricao(licitacaoItem.getDescricao() + " " + linha);
                    else licitacaoItem.setDescricao(linha);
                }
            }
            licitacaoItems.add(licitacaoItem);
        }
        return licitacaoItems;
    }

}
