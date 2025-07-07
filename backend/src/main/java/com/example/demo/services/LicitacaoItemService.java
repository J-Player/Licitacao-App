package com.example.demo.services;

import com.example.demo.models.entities.LicitacaoItem;
import com.example.demo.repositories.LicitacaoItemRepository;
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
public class LicitacaoItemService {

    private final LicitacaoItemRepository repository;

    public Mono<LicitacaoItem> findById(UUID id) {
        return repository.findById(id)
                .switchIfEmpty(monoResponseStatusNotFoundException())
                .onErrorResume(ex -> {
                    log.error("Ocorreu um erro ao recuperar o item (id = {}): {}", id, ex.getMessage());
                    return Mono.error(ex);
                });
    }

    public Mono<Page<LicitacaoItem>> findAllByLicitacaoId(UUID licitacaoId, Pageable pageable) {
        return repository.findAllByLicitacaoId(licitacaoId, pageable)
                .collectList()
                .zipWith(this.repository.count())
                .map(p -> new PageImpl<>(p.getT1(), pageable, p.getT2()));
    }

    public Mono<LicitacaoItem> save(LicitacaoItem licitacaoItem) {
        return repository.save(licitacaoItem)
                .doOnNext(l -> log.info("Item de licitação salvo com sucesso: {} | Licitação ID: {}", l.getId(), l.getLicitacaoId()));
    }

    public Mono<Void> update(LicitacaoItem licitacaoItem) {
        return findById(licitacaoItem.getId()).flatMap(repository::save)
                .doOnNext(l -> log.info("Item de licitação atualizado com sucesso: {} | Licitação ID: {}", l.getId(), l.getLicitacaoId()))
                .then();
    }

    public Mono<Void> delete(UUID id) {
        return findById(id)
                .map(repository::delete)
                .doOnNext(l -> log.info("Item de licitação excluído com sucesso"))
                .then();
    }

    private <T> Mono<T> monoResponseStatusNotFoundException() {
        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Item de licitação não encontrado"));
    }

}
