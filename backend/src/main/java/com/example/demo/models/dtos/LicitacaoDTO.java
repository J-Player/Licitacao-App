package com.example.demo.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LicitacaoDTO(
        @NotNull String unidade,
        @NotNull Integer uasg,
        @NotNull Integer numeroPregao,
        @NotNull String objeto,
        @NotNull String horarioEdital,
        @NotNull String endereco,
        String telefone,
        String fax,
        LocalDateTime entregaDaProposta
) {
}
