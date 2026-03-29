package com.taller.mecanico.Domain.DTOs;

import com.taller.mecanico.Persistence.Model.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InterveneOrderDTO(
    @NotBlank(message = "Reason is required")
    String reason,
    
    @NotNull(message = "Status is required")
    OrderStatus status
) {}
