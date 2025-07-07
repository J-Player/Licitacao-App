package com.example.demo.models.dtos;

import lombok.Builder;

import java.util.UUID;

@Builder
public record LicitacaoItemDTO(
        UUID licitacaoId,
        String nome,
        String descricao,
        String tratamentoDiferenciado,
        String aplicabilidadeDecreto7174) {
}
