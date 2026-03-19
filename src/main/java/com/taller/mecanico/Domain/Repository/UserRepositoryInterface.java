package com.taller.mecanico.Domain.Repository;

import java.util.List;

import com.taller.mecanico.Domain.DTOs.UserDTO;


public interface UserRepositoryInterface {

    List<UserDTO> getAll();

    UserDTO getById(Long id);

    UserDTO create(UserDTO user);

    UserDTO update(Long id, UserDTO user);

    void delete(Long id);

    UserDTO findByAccessCode(String accessCode);

}
