package com.taller.mecanico.Domain.Repository;

import java.util.List;

import com.taller.mecanico.Domain.DTOs.UserDTO;
import com.taller.mecanico.Persistence.Model.UserRole;
import com.taller.mecanico.Persistence.Projections.UserSummary;

public interface UserRepositoryInterface {
    List<UserDTO> getAll();
    List<UserSummary> getByName(String name);
    List<UserSummary> getByRole(UserRole role);
    UserDTO getById(Long id);
    UserDTO create(UserDTO user);
    UserDTO update(Long id, UserDTO user);
    void delete(Long id);
}
