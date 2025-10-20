package com.example.pib2.service;

import java.util.List;
import java.util.Optional;

import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.Category;

public interface CategoryService {
    List<Category> getCategoriesByBusiness(Business business);
    Optional<Category> getCategoryById(Long id);
    boolean delete(Long id);
}
