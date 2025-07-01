package com.example.demo.controllers;

import com.example.demo.models.entities.Licitacao;
import com.example.demo.services.ScrapingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scraper")
public class ScrapingController {

    private final ScrapingService service;

    @GetMapping("/start")
    public Flux<Licitacao> findAll() {
        return service.obterTodasAsLicitacoees();
    }

}
