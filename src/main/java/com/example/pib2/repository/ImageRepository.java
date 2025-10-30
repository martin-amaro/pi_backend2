package com.example.pib2.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pib2.model.entity.Image;
import com.example.pib2.model.entity.Product;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findFirstByProductAndMainTrue(Product product);
    List<Image> findByProduct(Product product);
    Image findByUrlAndProduct(String imageUrl, Product product);
}
