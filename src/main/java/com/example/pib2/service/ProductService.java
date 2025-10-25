package com.example.pib2.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    Page<Product> searchProducts(Business business, String query, Pageable pageable);
}
