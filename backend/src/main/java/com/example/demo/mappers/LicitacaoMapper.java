package com.example.demo.mappers;

import com.example.demo.models.dtos.LicitacaoDTO;
import com.example.demo.models.entities.Licitacao;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LicitacaoMapper {

    LicitacaoMapper INSTANCE = Mappers.getMapper(LicitacaoMapper.class);

    Licitacao toLicitacao(LicitacaoDTO licitacaoDTO);

    LicitacaoDTO toLicitacaoDTO(Licitacao licitacao);

}
