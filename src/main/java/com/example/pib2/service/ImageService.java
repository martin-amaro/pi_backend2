package com.example.pib2.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.example.pib2.model.entity.Image;
import com.example.pib2.model.entity.Product;

public interface ImageService {

    void save(Image image);
    void saveImageForProduct(String imageUrl, Product product);

    Map<String, Object> uploadFile(MultipartFile file);

    
}
