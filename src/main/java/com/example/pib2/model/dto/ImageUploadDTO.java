package com.example.pib2.model.dto;


import com.example.pib2.model.entity.Business;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ImageUploadDTO {
    @NotBlank(message = "La URL es obligatoria")
    private String url;

    private Business business;

}
