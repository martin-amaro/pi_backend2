package com.example.pib2.controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pib2.model.dto.ImageUploadDTO;
import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.Image;
import com.example.pib2.repository.ImageRepository;
import com.example.pib2.service.ImageService;
import com.example.pib2.util.AuthUtils;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    ImageService imageService;

    @Autowired
    ImageRepository imageRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestBody ImageUploadDTO dto) {
        try {
            Business business = AuthUtils.getCurrentBusiness();
            System.out.println(business.getName());
            System.out.println(business.getId());
            Image image = new Image();
            image.setBusiness(business);
            image.setUrl("https://example.com/image.jpg");
    
            imageRepository.save(image);
    
            return ResponseEntity.ok("Image uploaded successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
