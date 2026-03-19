package com.taller.mecanico.Domain.DTOs;

import com.taller.mecanico.Persistence.Model.UserRole;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record UserDTO(
    @Min(value = 1)
    Long id,
    @Size(max = 100)
    String name,
    UserRole role,
    @Size(min = 7, max = 20)
    String accessCode
) {}
