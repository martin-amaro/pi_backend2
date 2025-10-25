package com.example.pib2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByBusiness(Business business);
}
