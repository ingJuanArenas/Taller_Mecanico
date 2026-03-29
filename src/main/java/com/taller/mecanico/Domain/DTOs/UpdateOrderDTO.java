package com.taller.mecanico.Domain.DTOs;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;

public record UpdateOrderDTO(
    String description,
    @Min(value = 0, message = "Total amount cannot be negative")
    BigDecimal total,
    /** Si es true y la orden está en progreso, pasa a COMPLETED. Si es false o null, solo actualiza datos. */
    Boolean complete
) {}
