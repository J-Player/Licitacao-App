package com.example.demo.controllers;

import com.example.demo.mappers.LicitacaoMapper;
import com.example.demo.models.dtos.LicitacaoDTO;
import com.example.demo.models.entities.Licitacao;
import com.example.demo.services.LicitacaoService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/licitacao")
@Tag(name = "Licitação")
public class LicitacaoController {

    private final LicitacaoService service;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Consulta uma licitação através do ID")
    public Mono<Licitacao> findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @PageableAsQueryParam
    @Operation(summary = "Consulta uma lista licitação")
    public Mono<Page<Licitacao>> findAll(
            @RequestParam(required = false) Integer uasg,
            @RequestParam(required = false) Integer numeroPregao,
            @Parameter(hidden = true) Pageable pageable) {
        if (uasg != null && numeroPregao != null)
            return service.findAllByUasgAndNumeroPregao(uasg, numeroPregao, pageable);
        else if (uasg != null)
            return service.findAllByUasg(uasg, pageable);
        else if (numeroPregao != null)
            return service.findAllByNumeroPregao(numeroPregao, pageable);
        else return service.findAll(pageable);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria uma nova licitação no banco de dados")
    public Mono<Licitacao> save(@RequestBody LicitacaoDTO licitacaoDTO) {
        return service.save(LicitacaoMapper.INSTANCE.toLicitacao(licitacaoDTO));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Atualiza uma licitação no banco de dados")
    public Mono<Void> update(@RequestBody LicitacaoDTO licitacaoDTO, @PathVariable UUID id) {
        Licitacao licitacao = LicitacaoMapper.INSTANCE.toLicitacao(licitacaoDTO);
        licitacao.setId(id);
        return service.update(licitacao);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Exclui uma licitação do banco de dados")
    public Mono<Void> delete(@PathVariable UUID id) {
        return service.delete(id);
    }

}
