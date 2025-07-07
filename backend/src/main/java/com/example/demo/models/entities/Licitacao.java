package com.example.demo.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("licitacao")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Licitacao {

    @Id
    private UUID id;
    private String unidade;
    private Integer uasg;
    private Integer numeroPregao;
    private String objeto;
    private String horarioEdital;
    private String endereco;
    private String telefone;
    private String fax;
    private LocalDateTime entregaDaProposta;

}
