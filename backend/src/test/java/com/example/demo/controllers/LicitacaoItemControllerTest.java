package com.example.demo.controllers;

import com.example.demo.mappers.LicitacaoItemMapper;
import com.example.demo.models.dtos.LicitacaoItemDTO;
import com.example.demo.models.entities.Licitacao;
import com.example.demo.models.entities.LicitacaoItem;
import com.example.demo.services.LicitacaoItemService;
import com.example.demo.utils.LicitacaoItemCreator;
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
@DisplayName("Licitacao Item Controller Test")
@TestMethodOrder(MethodOrderer.MethodName.class)
class LicitacaoItemControllerTest {

    @InjectMocks
    private LicitacaoItemController licitacaoController;

    @Mock
    private LicitacaoItemService licitacaoService;

    private final LicitacaoItemDTO licitacaoItemDTO = LicitacaoItemCreator.LicitacaoItemDTO();
    private final LicitacaoItem licitacaoItem = LicitacaoItemMapper.INSTANCE.toLicitacaoItem(licitacaoItemDTO);

    @BeforeEach
    void setUp() {
        BDDMockito.when(licitacaoService.findById(any(UUID.class)))
                .thenReturn(Mono.just(licitacaoItem));
        BDDMockito.when(licitacaoService.findAllByLicitacaoId(any(UUID.class), any(Pageable.class)))
                .thenReturn(Mono.just(new PageImpl<>(List.of(licitacaoItem))));
        BDDMockito.when(licitacaoService.save(any(LicitacaoItem.class)))
                .thenReturn(Mono.just(licitacaoItem));
        BDDMockito.when(licitacaoService.update(any(LicitacaoItem.class)))
                .thenReturn(Mono.empty());
        BDDMockito.when(licitacaoService.delete(any(UUID.class)))
                .thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("findById | Retorna um item de licitação")
    void findById() {
        StepVerifier.create(licitacaoController.findById(UUID.randomUUID()))
                .expectSubscription()
                .expectNext(licitacaoItem)
                .verifyComplete();
    }

    @Test
    @DisplayName("findAllByLicitacaoId | Retorna um item de licitação através do código UASG e o número do pregão")
    void findAllByLicitacaoId() {
        StepVerifier.create(licitacaoController.findAllByLicitacaoId(UUID.randomUUID(), Pageable.unpaged()))
                .expectSubscription()
                .expectNext(new PageImpl<>(List.of(licitacaoItem)))
                .verifyComplete();
    }

    @Test
    @DisplayName("save | Cria um novo item de licitação")
    void save() {
        StepVerifier.create(licitacaoController.save(licitacaoItemDTO))
                .expectSubscription()
                .expectNext(licitacaoItem)
                .verifyComplete();
    }

    @Test
    @DisplayName("update | Atualiza um item de licitação")
    void update() {
        StepVerifier.create(licitacaoController.update(licitacaoItemDTO, UUID.randomUUID()))
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    @DisplayName("delete | Exclui um item de licitação do banco de dados")
    void delete() {
        StepVerifier.create(licitacaoController.delete(UUID.randomUUID()))
                .expectSubscription()
                .verifyComplete();
    }

}