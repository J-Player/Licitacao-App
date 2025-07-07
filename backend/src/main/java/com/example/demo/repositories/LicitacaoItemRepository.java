package com.example.demo.repositories;

import com.example.demo.models.entities.LicitacaoItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface LicitacaoItemRepository extends ReactiveCrudRepository<LicitacaoItem, UUID> {

    Flux<LicitacaoItem> findAllBy(Pageable pageable);
    Flux<LicitacaoItem> findAllByLicitacaoId(UUID licitacaoId, Pageable pageable);

}
