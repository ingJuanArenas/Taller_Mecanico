package com.taller.mecanico.Domain.DTOs;

import java.math.BigDecimal;

public record ProcedureResponseDTO(
    Long id,
    Long orderId,
    String name,
    String description,
    Integer executionTime,
    BigDecimal price
) {}
