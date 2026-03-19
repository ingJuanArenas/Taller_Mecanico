package com.taller.mecanico.Persistence.CRUDs;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taller.mecanico.Persistence.Model.User;


public interface UsersCRUD extends JpaRepository<User,Long> {
    
    Optional<User> findByAccessCode(String code);
    List<User> findByActiveTrue();
}
