package com.taller.mecanico.Persistence.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.taller.mecanico.Domain.DTOs.CreateOrderDTO;
import com.taller.mecanico.Domain.DTOs.OrderResponseDTO;
import com.taller.mecanico.Domain.DTOs.UpdateOrderDTO;
import com.taller.mecanico.Persistence.Model.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    
    @Mapping(source = "advisor.id", target = "advisorId")
    @Mapping(source = "mechanic.id", target = "mechanicId")
    OrderResponseDTO toDTO(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target =  "createdAt", ignore= true)
    @Mapping(target = "status", ignore = true)
    Order toEntity(CreateOrderDTO dto);
    void updateEntityFromDTO(UpdateOrderDTO dto, @MappingTarget Order order);

    List<OrderResponseDTO> toDTOs (List<Order> orders);
}
