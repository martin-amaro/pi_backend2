package com.example.pib2.model.dto;

import java.util.List;

// import java.util.Locale.Category;

import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductRequestDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private boolean active;
    private Long categoryId;
    private Integer thumbIndex;
    private List<String> removedImages;

    // @NotNull(message = "La categoría es obligatoria")
}
