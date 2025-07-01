package com.example.demo.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.models.entities.Licitacao;
import com.example.demo.repositories.LicitacaoRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class LicitacaoService {

    private final LicitacaoRepository repository;

    private final WebClient webClient = WebClient.builder().build();

    public Flux<Licitacao> findAll(Integer uasg, Integer numeroPregao) {
        return repository.findByUasgAndNumeroPregao(uasg, numeroPregao);
    }

    public Flux<Licitacao> findByUasg(Integer uasg) {
        return repository.findAllByUasg(uasg)
                .switchIfEmpty(getLicitacoes(uasg));
    }

    public Mono<Licitacao> getLicitacoes(Integer uasg) {
        final String URL = "https://dadosabertos.compras.gov.br/modulo-legado/1_consultarLicitacao";
        final LocalDateTime DATA_FINAL = LocalDateTime.now();
        final LocalDateTime DATA_INICIAL = DATA_FINAL.minusYears(1);
        final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("data_publicacao_inicial", DATA_INICIAL.format(DATE_FORMATTER));
        params.add("data_publicacao_final", DATA_FINAL.format(DATE_FORMATTER));
        if (uasg != null)
            params.add("uasg", uasg.toString());

        return webClient.get().uri(uriBuilder -> uriBuilder
                .path(URL)
                .queryParams(params)
                .build()).retrieve().bodyToMono(Licitacao.class);
    }

    public Mono<Licitacao> save(Licitacao licitacao) {
        return repository.save(licitacao)
                .doOnNext(l -> log.info("Licitação salva com sucesso: {}", l.getId()));
    }
}
