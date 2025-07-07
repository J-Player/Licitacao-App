package com.example.demo.controllers;

import com.example.demo.mappers.LicitacaoItemMapper;
import com.example.demo.models.dtos.LicitacaoItemDTO;
import com.example.demo.models.entities.LicitacaoItem;
import com.example.demo.services.LicitacaoItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/licitacao/item")
@Tag(name = "Licitação - Itens")
public class LicitacaoItemController {

    private final LicitacaoItemService service;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Consulta um item de licitação através do ID do item")
    public Mono<LicitacaoItem> findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping("/all")
    @PageableAsQueryParam
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Consulta uma lista de itens de uma licitação através do ID da licitação")
    public Mono<Page<LicitacaoItem>> findAllByLicitacaoId(@RequestParam UUID id,
                                                          @Parameter(hidden = true) Pageable pageable) {
        return service.findAllByLicitacaoId(id, pageable);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria um novo item de licitação do banco de dados")
    public Mono<LicitacaoItem> save(@RequestBody LicitacaoItemDTO licitacaoItemDTO) {
        return service.save(LicitacaoItemMapper.INSTANCE.toLicitacaoItem(licitacaoItemDTO));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Atualiza um item de licitação do banco de dados")
    public Mono<Void> update(@RequestBody LicitacaoItemDTO licitacaoItemDTO, @PathVariable UUID id) {
        LicitacaoItem licitacaoItem = LicitacaoItemMapper.INSTANCE.toLicitacaoItem(licitacaoItemDTO);
        licitacaoItem.setId(id);
        return service.update(licitacaoItem);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Exclui um item de licitação do banco de dados")
    public Mono<Void> delete(@PathVariable UUID id) {
        return service.delete(id);
    }

}
