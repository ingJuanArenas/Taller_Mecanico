package com.taller.mecanico.Domain.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record CreateProcedureDTO(
    @NotNull(message = "Order ID cannot be null")
    Long orderId,
    
    @NotBlank(message = "Name cannot be blank")
    String name,
    
    String description,
    
    @NotNull(message = "Execution time cannot be null")
    @Min(value = 1, message = "Execution time must be positive")
    Integer executionTime
) {}
