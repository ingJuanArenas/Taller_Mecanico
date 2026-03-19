package com.taller.mecanico.Persistence.Mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.taller.mecanico.Domain.DTOs.UserDTO;
import com.taller.mecanico.Persistence.Model.User;


@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserDTO toDTO(User user);

    User toEntity(UserDTO userDTO);
    List<UserDTO> toDTOs(List<User> users);

    void updateEntityFromDTO(UserDTO userDTO, @MappingTarget User user);
    
}