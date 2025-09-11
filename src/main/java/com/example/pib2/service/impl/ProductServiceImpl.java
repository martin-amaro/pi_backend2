package com.example.pib2.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.pib2.model.dto.ProductRequestDTO;
import com.example.pib2.model.dto.ProductResponseDTO;
import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.Category;
import com.example.pib2.model.entity.Product;
import com.example.pib2.repository.CategoryRepository;
import com.example.pib2.repository.ProductRepository;
import com.example.pib2.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Product createProduct(ProductRequestDTO dto, Business business) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setBusiness(business);

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        product.setCategory(category);

        return productRepository.save(product);
    }

    // Mapper interno para uso desde el controlador
    public ProductResponseDTO mapToDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setCategoryName(product.getCategory().getName());
        dto.setBusinessName(product.getBusiness().getName());
        return dto;
    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product updateProduct(Product producto) {
        return productRepository.save(producto);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public long countProducts() {
        return productRepository.count();
    }
}
