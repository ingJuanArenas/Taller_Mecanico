package com.taller.mecanico.Domain.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.taller.mecanico.Domain.DTOs.UserDTO;
import com.taller.mecanico.Domain.Exceptions.AlreadyExistsException;
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

    public UserDTO getById(Long id){
        return userRepository.getById(id);
    }

    public UserDTO findByAccessCode(String code){
        return userRepository.findByAccessCode(code);
       
    }


    public UserDTO create(UserDTO user){
       var exists=  userRepository.existById(user.id());
       if (exists) throw new AlreadyExistsException("Id already registered");
        return userRepository.create(user);
    }

    public UserDTO update(Long id, UserDTO user){
        return userRepository.update(id, user);
    }

    public void delete(Long id){
        userRepository.delete(id);
    }
}
