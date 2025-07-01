package com.example.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.entities.Licitacao;
import com.example.demo.services.LicitacaoService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/licitacoes")
public class LicitacaoController {

    private final LicitacaoService service;

    @GetMapping
    public Flux<Licitacao> findAll(@RequestParam(required = false) Integer uasg,
                                   @RequestParam(required = false) Integer numeroPregao) {
        return service.findAll(uasg, numeroPregao);
    }

}
