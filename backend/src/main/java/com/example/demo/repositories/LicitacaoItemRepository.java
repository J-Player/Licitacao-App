package com.example.demo.repositories;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.example.demo.models.entities.LicitacaoItem;

import reactor.core.publisher.Flux;

public interface LicitacaoItemRepository extends ReactiveCrudRepository<LicitacaoItem, UUID> {

    Flux<LicitacaoItem> findAllByLicitacaoId(String idCompra);

}
