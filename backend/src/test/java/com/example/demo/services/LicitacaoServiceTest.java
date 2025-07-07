package com.example.demo.services;

import com.example.demo.models.entities.Licitacao;
import com.example.demo.repositories.LicitacaoRepository;
import com.example.demo.utils.LicitacaoCreator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(SpringExtension.class)
@DisplayName("Licitacao Service Test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class LicitacaoServiceTest {

    @InjectMocks
    private LicitacaoService licitacaoService;

    @Mock
    private LicitacaoRepository licitacaoRepository;

    private final Licitacao licitacao = LicitacaoCreator.licitacao();

    @BeforeEach
    void setUp() {
        licitacao.setId(UUID.randomUUID());
        BDDMockito.when(licitacaoRepository.findById(any(UUID.class)))
                .thenReturn(Mono.just(licitacao));
        BDDMockito.when(licitacaoRepository.findAllByUasgAndNumeroPregao(anyInt(), anyInt(), any(Pageable.class)))
                .thenReturn(Flux.just(licitacao));
        BDDMockito.when(licitacaoRepository.findAllByUasg(anyInt(), any(Pageable.class)))
                .thenReturn(Flux.just(licitacao));
        BDDMockito.when(licitacaoRepository.findAllByNumeroPregao(anyInt(), any(Pageable.class)))
                .thenReturn(Flux.just(licitacao));
        BDDMockito.when(licitacaoRepository.findAllBy(any(Pageable.class)))
                .thenReturn(Flux.just(licitacao));
        BDDMockito.when(licitacaoRepository.count())
                .thenReturn(Mono.just(1L));
        BDDMockito.when(licitacaoRepository.save(any(Licitacao.class)))
                .thenReturn(Mono.just(licitacao));
        BDDMockito.when(licitacaoRepository.delete(any(Licitacao.class)))
                .thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("findById | Retorna uma licitação através do ID")
    void findById_ReturnMonoLicitacao_WhenSuccessful() {
        StepVerifier.create(licitacaoService.findById(UUID.randomUUID()))
                .expectSubscription()
                .expectNext(licitacao)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById | Retorna um error quando a licitação não existe")
    void findById_ReturnMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(licitacaoRepository.findById(any(UUID.class)))
                .thenReturn(Mono.empty());
        StepVerifier.create(licitacaoService.findById(UUID.randomUUID()))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("findAllByUasg | Retorna uma lista de licitação através do código UASG")
    void findAllByUasg_ReturnMonoLicitacao_WhenSuccessful() {
        StepVerifier.create(licitacaoService.findAllByUasg(1, Pageable.unpaged()))
                .expectSubscription()
                .expectNext(new PageImpl<>(List.of(licitacao)))
                .verifyComplete();
    }
    @Test
    @DisplayName("findAllByNumeroPregao | Retorna uma lista de licitação através do número do pregão")
    void findAllByNumeroPregao_ReturnMonoLicitacao_WhenSuccessful() {
        StepVerifier.create(licitacaoService.findAllByNumeroPregao(1, Pageable.unpaged()))
                .expectSubscription()
                .expectNext(new PageImpl<>(List.of(licitacao)))
                .verifyComplete();
    }

    @Test
    @DisplayName("findAllByUasgAndNumeroPregao | Retorna uma lista de licitação através do código UASG e o número do pregão")
    void findAllByUasgAndNumeroPregao_ReturnMonoLicitacao_WhenSuccessful() {
        StepVerifier.create(licitacaoService.findAllByUasgAndNumeroPregao(1, 2, Pageable.unpaged()))
                .expectSubscription()
                .expectNext(new PageImpl<>(List.of(licitacao)))
                .verifyComplete();
    }

    @Test
    @DisplayName("findAll | Retorna uma lista de licitações")
    void findAll_ReturnFluxOfLicitacao_WhenSuccessful() {
        StepVerifier.create(licitacaoService.findAll(Pageable.unpaged()))
                .expectSubscription()
                .expectNext(new PageImpl<>(List.of(licitacao)))
                .verifyComplete();
    }

    @Test
    @DisplayName("save | Cria uma nova licitação no banco de dados")
    void save() {
        StepVerifier.create(licitacaoService.save(licitacao))
                .expectSubscription()
                .expectNext(licitacao)
                .verifyComplete();
    }

    @Test
    @DisplayName("update | Atualiza uma licitação no banco de dados")
    void update() {
        StepVerifier.create(licitacaoService.update(licitacao))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("update | Retorna um erro quando a licitação não existe")
    void update_ReturnsMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(licitacaoRepository.findById(any(UUID.class)))
                .thenReturn(Mono.empty());
        StepVerifier.create(licitacaoService.update(licitacao))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("delete | Exclui uma licitação do banco de dados")
    void delete() {
        StepVerifier.create(licitacaoService.delete(UUID.randomUUID()))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("delete | Retorna um erro quando a licitação não existe")
    void delete_ReturnsMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(licitacaoRepository.findById(any(UUID.class)))
                .thenReturn(Mono.empty());
        StepVerifier.create(licitacaoService.delete(UUID.randomUUID()))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

}