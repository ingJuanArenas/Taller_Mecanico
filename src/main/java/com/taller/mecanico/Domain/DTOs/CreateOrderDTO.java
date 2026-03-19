package com.taller.mecanico.Domain.DTOs;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;


public record CreateOrderDTO(
    
    Long customerId,
    @Size(max = 8, message = "Vehicle Plate must be between 1 and 8 characters")
    String vehiclePlate,
    @Size(max = 100, message = "Vehicle model must be between 1 and 100 characters")
    String vehicleModel,
    String description,
    @Min(value = 1, message = "Total amount must be greater than 1")
    BigDecimal total,
    @Min(value = 1, message = "Advisor Id must be greater than 1")
    Long advisorId
) {}


