package com.example.pib2.model.dto;

import lombok.Data;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String categoryName;
    private String businessName;

    // Getters y Setters
}