package com.taller.mecanico.Domain.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.taller.mecanico.Domain.DTOs.UserDTO;
import com.taller.mecanico.Persistence.Model.UserRole;
import com.taller.mecanico.Persistence.Projections.UserSummary;
import com.taller.mecanico.Persistence.Repository.UserRepositoryImpl;

@Service
public class UserService {
    
    private final UserRepositoryImpl userRepository;

    public UserService(UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getAll(){
        return userRepository.getAll();
    }

    public List<UserSummary> getByName(String name){
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        return userRepository.getByName(name);
    }

    public List<UserSummary> getByRole(UserRole role){
        return userRepository.getByRole(role);
    }

    public UserDTO getById(Long id){
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0");
        }
        return userRepository.getById(id);
    }

    public UserDTO create(UserDTO user){
        return userRepository.create(user);
    }

    public UserDTO update(Long id, UserDTO user){
        return userRepository.update(id, user);
    }

    public void delete(Long id){
        userRepository.delete(id);
    }
}
