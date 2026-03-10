package com.taller.mecanico.Domain.DTOs;

import com.taller.mecanico.Persistence.Model.UserRole;

public record UserDTO(
    Long id,
    String name,
    UserRole role,
    String password
) {}
