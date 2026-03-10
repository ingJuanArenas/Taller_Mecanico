package com.taller.mecanico.Domain.DTOs;

import java.math.BigDecimal;

import com.taller.mecanico.Persistence.Model.OrderStatus;

public record UpdateOrderDTO(
    String description,
    BigDecimal total,
    OrderStatus status
) {}
