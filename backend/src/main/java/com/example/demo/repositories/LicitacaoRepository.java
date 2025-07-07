package com.example.demo.repositories;

import com.example.demo.models.entities.Licitacao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface LicitacaoRepository extends ReactiveCrudRepository<Licitacao, UUID>, ReactiveSortingRepository<Licitacao, UUID> {

    Flux<Licitacao> findAllByUasgAndNumeroPregao(Integer uasg, Integer numeroPregao, Pageable pageable);

    Flux<Licitacao> findAllByUasg(Integer uasg, Pageable pageable);

    Flux<Licitacao> findAllByNumeroPregao(Integer numeroPregao, Pageable pageable);

    Flux<Licitacao> findAllBy(Pageable pageable);
}
