package com.example.demo.services;

import com.example.demo.models.entities.LicitacaoItem;
import com.example.demo.repositories.LicitacaoItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LicitacaoItemService {

    private final LicitacaoItemRepository repository;

    public Flux<LicitacaoItem> findAll() {
        return repository.findAll();
    }

    public Flux<LicitacaoItem> findByLicitacaoId(String licitacaoId) {
        return repository.findAllByLicitacaoId(licitacaoId);
    }

    public Mono<LicitacaoItem> save(LicitacaoItem licitacaoItem) {
        return repository.save(licitacaoItem);
    }
}
