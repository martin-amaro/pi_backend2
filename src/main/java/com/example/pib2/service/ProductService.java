package com.example.pib2.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.pib2.model.dto.ProductRequestDTO;
import com.example.pib2.model.dto.ProductResponseDTO;
import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.Product;

public interface ProductService {

    Product createProduct(ProductRequestDTO dto, Business business);

    Product updateProduct(Product product, ProductRequestDTO dto, Business business);

    List<Product> getAll();

    Optional<Product> findById(Long id);

   
    void deleteProduct(Long id);

    long countProducts();

    ProductResponseDTO mapToDTO(Product product);

    Page<Product> searchProducts(Business business, String query, Long categoryId, Pageable pageable);

}
