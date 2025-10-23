package com.example.pib2.service;

import java.util.List;
import java.util.Optional;

import com.example.pib2.model.dto.ProductRequestDTO;
import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.Product;

public interface ProductService {

    Product createProduct(ProductRequestDTO dto, Business business);

    List<Product> getAll();

    Optional<Product> findById(Long id);

    Product updateProduct(Product producto);

    void deleteProduct(Long id);

    long countProducts();

    //Product createProductWithImages(ProductRequestDTO request, MultipartFile[] images, Business business);
}
