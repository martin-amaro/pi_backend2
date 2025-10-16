package com.example.pib2.model.dto;

import com.example.pib2.model.entity.UserRole;

public record UserLoginResponseDTO(Long id, String name, String email, UserRole role, String provider, String token, Long businessId, String planName, String subscriptionStatus) {
    
}
