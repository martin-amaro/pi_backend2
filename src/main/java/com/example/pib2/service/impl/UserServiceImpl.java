package com.example.pib2.service.impl;

import com.example.pib2.model.dto.UserPatchDTO;
import com.example.pib2.model.entity.User;
import com.example.pib2.repository.UserRepository;
import com.example.pib2.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean delete(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }

    @Override
    public Optional<User> findByProviderId(String providerId) {
        return userRepository.findByProviderId(providerId);
    }

    @Override
    public Optional<User> updatePartialByEmail(String email, UserPatchDTO dto) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        User user = optionalUser.get();

        // Actualizar nombre
        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        // Actualizar email
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            String newEmail = dto.getEmail();

            // Verificar si el nuevo email ya está siendo usado por otro usuario
            Optional<User> existingByEmail = userRepository.findByEmail(newEmail);
            if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(user.getId())) {
                throw new IllegalArgumentException("El email ya está en uso por otro usuario");
            }

            user.setEmail(newEmail);
        }

        // Actualizar contraseña (y encriptar)
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            String hashedPassword = passwordEncoder.encode(dto.getPassword());
            user.setPassword(hashedPassword);
        }

        userRepository.save(user);
        return Optional.of(user);
    }

}
