package com.example.pib2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.Category;


public interface CategoryRepository extends JpaRepository<Category, Long>{
    List<Category> findByBusiness(Business business);
}
