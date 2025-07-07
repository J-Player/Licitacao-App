package com.example.demo.mappers;

import com.example.demo.models.dtos.LicitacaoDTO;
import com.example.demo.models.dtos.LicitacaoItemDTO;
import com.example.demo.models.entities.LicitacaoItem;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LicitacaoItemMapper {

    LicitacaoItemMapper INSTANCE = Mappers.getMapper(LicitacaoItemMapper.class);

    LicitacaoItem toLicitacaoItem(LicitacaoItemDTO licitacaoItemDTO);

    LicitacaoDTO toLicitacaoDTO(LicitacaoItem licitacaoItem);

}
