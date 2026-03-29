package com.taller.mecanico.Domain.DTOs;

import java.math.BigDecimal;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateProcedurePriceDTO(
    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price must be non-negative")
    BigDecimal price
) {}
