package com.example.demo.repositories;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.models.entities.Licitacao;

import reactor.core.publisher.Flux;

@Repository
public interface LicitacaoRepository extends ReactiveCrudRepository<Licitacao, UUID> {

    Flux<Licitacao> findAllByUasg(Integer id);

    Flux<Licitacao> findByUasgAndNumeroPregao(Integer uasg, Integer numeroPregao);

}
