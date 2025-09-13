package com.example.pib2.service;

import java.util.List;
import java.util.Optional;

import com.example.pib2.model.dto.UserPatchDTO;
// import com.example.pib2.model.dto.UserDTO;
import com.example.pib2.model.entity.User;

public interface UserService {
    List<User> getAll();
    Optional<User> getById(Long id);
    User save(User user);
    boolean delete(Long id);
    boolean existsByEmail(String email);
    
    Optional<User> findByEmail(String email);
    Optional<User> findByProviderId(String providerId);
    Optional<User> updatePartialByEmail(String email, UserPatchDTO dto);


    void deleteAll();
}
