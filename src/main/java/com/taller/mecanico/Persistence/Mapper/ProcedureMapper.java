package com.taller.mecanico.Persistence.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.taller.mecanico.Domain.DTOs.CreateProcedureDTO;
import com.taller.mecanico.Domain.DTOs.ProcedureResponseDTO;
import com.taller.mecanico.Domain.DTOs.UpdateProcedurePriceDTO;
import com.taller.mecanico.Persistence.Model.Procedure;

@Mapper(componentModel = "spring")
public interface ProcedureMapper {
    
    @Mapping(source = "order.id", target = "orderId")
    ProcedureResponseDTO toDTO(Procedure procedure);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "price", ignore = true)
    Procedure toEntity(CreateProcedureDTO dto);
    
    void updateEntityFromDTO(UpdateProcedurePriceDTO dto, @MappingTarget Procedure procedure);

    List<ProcedureResponseDTO> toDTOs(List<Procedure> procedures);
}
