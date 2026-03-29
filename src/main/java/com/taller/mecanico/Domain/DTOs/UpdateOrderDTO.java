package com.taller.mecanico.Domain.DTOs;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;

public record UpdateOrderDTO(
    String description,
    @Min(value = 1, message = "Total amount must be greater than 1")
    BigDecimal total,
    /** Si es true y la orden está en progreso, pasa a COMPLETED. Si es false o null, solo actualiza datos. */
    Boolean complete
) {}
