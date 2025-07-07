package com.example.demo.services;

import com.example.demo.models.entities.Licitacao;
import com.example.demo.repositories.LicitacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LicitacaoService {

    private final LicitacaoRepository repository;

    public Mono<Licitacao> findById(UUID id) {
        return repository.findById(id)
                .switchIfEmpty(monoResponseStatusNotFoundException())
                .onErrorResume(ex -> {
                    log.error("Ocorreu um erro ao recuperar o item (id = {}): {}", id, ex.getMessage());
                    return Mono.error(ex);
                });
    }

    public Mono<Page<Licitacao>> findAllByUasg(Integer uasg, Pageable pageable) {
        return repository.findAllByUasg(uasg, pageable)
                .collectList()
                .zipWith(this.repository.count())
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    public Mono<Page<Licitacao>> findAllByNumeroPregao(Integer numeroPregao, Pageable pageable) {
        return repository.findAllByNumeroPregao(numeroPregao, pageable)
                .collectList()
                .zipWith(this.repository.count())
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));

    }

    public Mono<Page<Licitacao>> findAllByUasgAndNumeroPregao(Integer uasg, Integer numeroPregao, Pageable pageable) {
        return repository.findAllByUasgAndNumeroPregao(uasg, numeroPregao, pageable)
                .collectList()
                .zipWith(this.repository.count())
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    public Mono<Page<Licitacao>> findAll(Pageable pageable) {
        return repository.findAllBy(pageable)
                .collectList()
                .zipWith(this.repository.count())
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    public Mono<Licitacao> save(Licitacao licitacao) {
        return repository.save(licitacao)
                .doOnNext(l -> log.info("Licitação salva com sucesso: {}", l.getId()));
    }

    public Mono<Void> update(Licitacao licitacao) {
        return findById(licitacao.getId())
                .flatMap(repository::save)
                .doOnNext(l -> log.info("Licitação atualizada com sucesso: {}", l.getId()))
                .then();
    }

    public Mono<Void> delete(UUID id) {
        return findById(id).map(repository::delete).then();
    }

    private <T> Mono<T> monoResponseStatusNotFoundException() {
        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Licitação não encontrada"));
    }
}
