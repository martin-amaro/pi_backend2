package com.example.pib2.model.dto;

import com.example.pib2.model.entity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
}

