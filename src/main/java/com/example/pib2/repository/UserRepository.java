package com.example.pib2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByBusiness(Business business);

    boolean existsByEmail(String email);

     Optional<User> findByProviderId(String providerId);

}