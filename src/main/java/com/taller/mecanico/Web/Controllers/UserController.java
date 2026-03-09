package com.taller.mecanico.Web.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taller.mecanico.Domain.DTOs.UserDTO;
import com.taller.mecanico.Domain.Service.UserService;
import com.taller.mecanico.Persistence.Model.UserRole;
import com.taller.mecanico.Persistence.Projections.UserSummary;

@RestController
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll(){
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<UserSummary>> getByName(@RequestParam("name") String name){
        return ResponseEntity.ok(userService.getByName(name));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserSummary>> getByRole(@PathVariable UserRole role){
        return ResponseEntity.ok(userService.getByRole(role));
    }

    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody UserDTO user){
        return ResponseEntity.ok(userService.create(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO user){
        return ResponseEntity.ok(userService.update(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    
}
