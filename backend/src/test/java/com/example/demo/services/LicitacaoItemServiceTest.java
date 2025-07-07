package com.example.demo.services;

import com.example.demo.models.entities.LicitacaoItem;
import com.example.demo.repositories.LicitacaoItemRepository;
import com.example.demo.utils.LicitacaoItemCreator;
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

@ExtendWith(SpringExtension.class)
@DisplayName("Licitacao Item Service Test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class LicitacaoItemServiceTest {

    @InjectMocks
    private LicitacaoItemService service;

    @Mock
    private LicitacaoItemRepository repository;

    private final LicitacaoItem licitacaoItem = LicitacaoItemCreator.licitacaoItem();

    @BeforeEach
    void setUp() {
        licitacaoItem.setId(UUID.randomUUID());
        BDDMockito.when(repository.findAllByLicitacaoId(any(UUID.class), any(Pageable.class)))
                .thenReturn(Flux.just(licitacaoItem));
        BDDMockito.when(repository.findById(any(UUID.class)))
                .thenReturn(Mono.just(licitacaoItem));
        BDDMockito.when(repository.findAllBy(any(Pageable.class)))
                .thenReturn(Flux.just(licitacaoItem));
        BDDMockito.when(repository.count())
                .thenReturn(Mono.just(1L));
        BDDMockito.when(repository.save(any(LicitacaoItem.class)))
                .thenReturn(Mono.just(licitacaoItem));
        BDDMockito.when(repository.delete(any(LicitacaoItem.class)))
                .thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("findById | Retorna um item de licitação através do ID")
    void findById_ReturnMonoLicitacao_WhenSuccessful() {
        StepVerifier.create(service.findById(UUID.randomUUID()))
                .expectSubscription()
                .expectNext(licitacaoItem)
                .verifyComplete();
    }

    @Test
    @DisplayName("findById | Retorna um error quando a licitação não existe")
    void findById_ReturnMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(repository.findById(any(UUID.class)))
                .thenReturn(Mono.empty());
        StepVerifier.create(service.findById(UUID.randomUUID()))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("findAllByLicitacaoId | Retorna uma lista de itens através do ID da licitação")
    void findAllByLicitacaoId_ReturnMonoLicitacao_WhenSuccessful() {
        StepVerifier.create(service.findAllByLicitacaoId(UUID.randomUUID(), Pageable.unpaged()))
                .expectSubscription()
                .expectNext(new PageImpl<>(List.of(licitacaoItem)))
                .verifyComplete();
    }

    @Test
    @DisplayName("save | Cria um novo item de licitação no banco de dados")
    void save() {
        StepVerifier.create(service.save(licitacaoItem))
                .expectSubscription()
                .expectNext(licitacaoItem)
                .verifyComplete();
    }

    @Test
    @DisplayName("update | Atualiza um item de licitação no banco de dados")
    void update() {
        StepVerifier.create(service.update(licitacaoItem))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("update | Retorna um erro quando o item de licitação não existe")
    void update_ReturnsMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(repository.findById(any(UUID.class)))
                .thenReturn(Mono.empty());
        StepVerifier.create(service.update(licitacaoItem))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("delete | Exclui um item de licitação do banco de dados")
    void delete() {
        StepVerifier.create(service.delete(UUID.randomUUID()))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("delete | Retorna um erro quando o item de licitação não existe")
    void delete_ReturnsMonoError_WhenEmptyMonoIsReturned() {
        BDDMockito.when(repository.findById(any(UUID.class)))
                .thenReturn(Mono.empty());
        StepVerifier.create(service.delete(UUID.randomUUID()))
                .expectSubscription()
                .expectError(ResponseStatusException.class)
                .verify();
    }

}