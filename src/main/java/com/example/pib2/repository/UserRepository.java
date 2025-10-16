package com.example.pib2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByBusiness(Business business);

    boolean existsByEmail(String email);

    Optional<User> findByProviderId(String providerId);

    @Query("SELECT u FROM User u WHERE u.business = :business AND " +
            "(LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<User> searchByBusinessAndNameOrEmail(
            @Param("business") Business business,
            @Param("query") String query,
            Pageable pageable);

}