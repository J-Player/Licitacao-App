package com.example.demo.models.entities;

import java.util.UUID;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("licitacaoItem")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LicitacaoItem {

    @Id
    private UUID id;
    private UUID licitacaoId;
    private String nome;
    private String descricao;
    private String tratamentoDiferenciado;
    private String aplicabilidadeDecreto7174;
    private String aplicabilidadeMargemDePreferencia;
    private Integer quantidade;
    private String unidadeDeFornecimento;

}
