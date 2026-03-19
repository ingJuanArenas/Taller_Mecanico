package com.taller.mecanico.Persistence.Repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.taller.mecanico.Domain.DTOs.UserDTO;
import com.taller.mecanico.Domain.Exceptions.BadRequestException;
import com.taller.mecanico.Domain.Exceptions.NotFoundException;
import com.taller.mecanico.Domain.Repository.UserRepositoryInterface;
import com.taller.mecanico.Persistence.CRUDs.UsersCRUD;
import com.taller.mecanico.Persistence.Mapper.UserMapper;


@Repository
public class UserRepositoryImpl implements UserRepositoryInterface {

    private final UsersCRUD usersCRUD;
    private final UserMapper userMapper;

    public UserRepositoryImpl(UsersCRUD usersCRUD, UserMapper userMapper) {
        this.usersCRUD = usersCRUD;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserDTO> getAll() {
        var users = usersCRUD.findByActiveTrue();
        if (users.isEmpty()) {
            throw new NotFoundException("No content found");
        }
        return userMapper.toDTOs(users);
    }

    @Override
    public UserDTO getById(Long id) {
        var user = usersCRUD.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO create(UserDTO user) {
        var newUser= userMapper.toEntity(user);
        var savedUser= usersCRUD.save(newUser);
        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO update(Long id, UserDTO user) {
        var existingUser = usersCRUD.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        userMapper.updateEntityFromDTO(user, existingUser);
        var updatedUser = usersCRUD.save(existingUser);
        return userMapper.toDTO(updatedUser);
    }

    @Override
    public void delete(Long id) {
        var user = usersCRUD.findById(id)
            .orElseThrow(() -> new NotFoundException("User not found"));
        user.setActive(false);
        usersCRUD.save(user);
    }

    @Override
    public UserDTO findByAccessCode(String accessCode) {
        var user = usersCRUD.findByAccessCode(accessCode).orElseThrow(() -> new NotFoundException("User not found"));
        if (!user.getActive()) {
        throw new BadRequestException("Usuario inactivo");
        }
        return userMapper.toDTO(user);
    }

   public boolean existById(Long id) {
        return usersCRUD.existsById(id);
   }
    
}


    