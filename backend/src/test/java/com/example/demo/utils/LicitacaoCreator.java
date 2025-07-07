package com.example.demo.utils;

import com.example.demo.models.dtos.LicitacaoDTO;
import com.example.demo.models.entities.Licitacao;

public class LicitacaoCreator {

    protected static final String NAME = "Licitacao";

    public static Licitacao licitacao() {
        return Licitacao.builder()
                .unidade(NAME)
                .build();
    }

    public static LicitacaoDTO licitacaoDTO() {
        return LicitacaoDTO.builder()
                .unidade(NAME)
                .build();
    }

    public static LicitacaoDTO invalidLicitacaoDTO() {
        return LicitacaoDTO.builder().unidade(null).build();
    }

    public static Licitacao LicitacaoToRead() {
        Licitacao licitacao = licitacao();
        licitacao.setUnidade(NAME.concat("_to_read"));
        return licitacao;
    }

    public static Licitacao licitacaoToUpdate() {
        Licitacao licitacao = licitacao();
        licitacao.setUnidade(NAME.concat("_to_update"));
        return licitacao;
    }

    public static Licitacao licitacaoToDelete() {
        Licitacao licitacao = licitacao();
        licitacao.setUnidade(NAME.concat("_to_delete"));
        return licitacao;
    }

}
