package com.example.demo.utils;

import com.example.demo.models.dtos.LicitacaoItemDTO;
import com.example.demo.models.entities.LicitacaoItem;

public class LicitacaoItemCreator {

    protected static final String NAME = "Licitacao_Item";

    public static LicitacaoItem licitacaoItem() {
        return LicitacaoItem.builder()
                .nome(NAME)
                .build();
    }

    public static LicitacaoItemDTO LicitacaoItemDTO() {
        return LicitacaoItemDTO.builder()
                .nome(NAME)
                .build();
    }

    public static LicitacaoItemDTO invalidLicitacaoDTO() {
        return LicitacaoItemDTO.builder().nome(null).build();
    }

    public static LicitacaoItem LicitacaoToRead() {
        LicitacaoItem licitacaoItem = licitacaoItem();
        licitacaoItem.setNome(NAME.concat("_to_read"));
        return licitacaoItem;
    }

    public static LicitacaoItem licitacaoToUpdate() {
        LicitacaoItem licitacaoItem = licitacaoItem();
        licitacaoItem.setNome(NAME.concat("_to_update"));
        return licitacaoItem;
    }

    public static LicitacaoItem licitacaoToDelete() {
        LicitacaoItem licitacaoItem = licitacaoItem();
        licitacaoItem.setNome(NAME.concat("_to_delete"));
        return licitacaoItem;
    }

}
