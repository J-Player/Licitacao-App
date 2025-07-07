package com.example.demo.controllers;

import com.example.demo.mappers.LicitacaoMapper;
import com.example.demo.models.dtos.LicitacaoDTO;
import com.example.demo.models.entities.Licitacao;
import com.example.demo.services.LicitacaoService;
import com.example.demo.utils.LicitacaoCreator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(SpringExtension.class)
@DisplayName("Licitacao Controller Test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class LicitacaoControllerTest {

    @InjectMocks
    private LicitacaoController licitacaoController;

    @Mock
    private LicitacaoService licitacaoService;

    private final LicitacaoDTO licitacaoDTO = LicitacaoCreator.licitacaoDTO();
    private final Licitacao licitacao = LicitacaoMapper.INSTANCE.toLicitacao(licitacaoDTO);

    @BeforeEach
    void setUp() {
        BDDMockito.when(licitacaoService.findById(any(UUID.class)))
                .thenReturn(Mono.just(licitacao));
        BDDMockito.when(licitacaoService.findAllByUasgAndNumeroPregao(anyInt(), anyInt(), any(Pageable.class)))
                .thenReturn(Mono.just(new PageImpl<>(List.of(licitacao))));
        BDDMockito.when(licitacaoService.findAllByUasg(anyInt(), any(Pageable.class)))
                .thenReturn(Mono.just(new PageImpl<>(List.of(licitacao))));
        BDDMockito.when(licitacaoService.findAllByNumeroPregao(anyInt(), any(Pageable.class)))
                .thenReturn(Mono.just(new PageImpl<>(List.of(licitacao))));
        BDDMockito.when(licitacaoService.findAll(any(Pageable.class)))
                .thenReturn(Mono.just(new PageImpl<>(List.of(licitacao))));
        BDDMockito.when(licitacaoService.findAllByUasg(anyInt(), any(Pageable.class)))
                .thenReturn(Mono.just(new PageImpl<>(List.of(licitacao))));
        BDDMockito.when(licitacaoService.save(any(Licitacao.class)))
                .thenReturn(Mono.just(licitacao));
        BDDMockito.when(licitacaoService.update(any(Licitacao.class)))
                .thenReturn(Mono.empty());
        BDDMockito.when(licitacaoService.delete(any(UUID.class)))
                .thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("findById | Retorna uma licitação")
    void findById() {
        StepVerifier.create(licitacaoController.findById(UUID.randomUUID()))
                .expectSubscription()
                .expectNext(licitacao)
                .verifyComplete();
    }

    @Test
    @DisplayName("findAllByUasg | Retorna uma lista de licitações filtrada pelo código UASG")
    void findAllByUasg() {
        StepVerifier.create(licitacaoController.findAll(1, null, Pageable.unpaged()))
                .expectSubscription()
                .expectNext(new PageImpl<>(List.of(licitacao)))
                .verifyComplete();
    }

    @Test
    @DisplayName("findAllByNumeroPregao | Retorna uma lista de licitações filtrada pelo número do pregão")
    void findAllByNumeroPregao() {
        StepVerifier.create(licitacaoController.findAll(null, 2, Pageable.unpaged()))
                .expectSubscription()
                .expectNext(new PageImpl<>(List.of(licitacao)))
                .verifyComplete();
    }

    @Test
    @DisplayName("findAll | Retorna uma lista de licitação")
    void findAll() {
        StepVerifier.create(licitacaoController.findAll(null, null, Pageable.unpaged()))
                .expectSubscription()
                .expectNext(new PageImpl<>(List.of(licitacao)))
                .verifyComplete();
    }

    @Test
    @DisplayName("findAllByUasgAndNumeroPregao | Retorna uma lista de licitação filtrada pelo código UASG e o número do pregão")
    void findAllByUasgAndNumeroPregao() {
        StepVerifier.create(licitacaoController.findAll(1, 2, Pageable.unpaged()))
                .expectSubscription()
                .expectNext(new PageImpl<>(List.of(licitacao)))
                .verifyComplete();
    }

    @Test
    @DisplayName("save | Cria uma nova licitação")
    void save() {
        StepVerifier.create(licitacaoController.save(licitacaoDTO))
                .expectSubscription()
                .expectNext(licitacao)
                .verifyComplete();
    }

    @Test
    @DisplayName("update | Atualiza uma licitação")
    void update() {
        StepVerifier.create(licitacaoController.update(licitacaoDTO, UUID.randomUUID()))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("delete | Exclui uma licitação do banco de dados")
    void delete() {
        StepVerifier.create(licitacaoController.delete(UUID.randomUUID()))
                .expectSubscription()
                .verifyComplete();
    }

}