package com.taller.mecanico.Domain.DTOs;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.taller.mecanico.Persistence.Model.OrderStatus;

public record OrderResponseDTO(
    Long id,
    LocalDateTime createdAt,
    Long customerId,
    String vehiclePlate,
    String vehicleModel,
    String description,
    BigDecimal total,
    OrderStatus status,
    Long advisorId,
    Long mechanicId
) {}
