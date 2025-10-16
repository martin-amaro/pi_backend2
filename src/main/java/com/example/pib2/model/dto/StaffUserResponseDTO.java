package com.example.pib2.model.dto;

import com.example.pib2.model.entity.UserRole;

public record StaffUserResponseDTO(Long id, String name, String email, UserRole role) {

}