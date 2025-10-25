package com.example.pib2.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.Category;
import com.example.pib2.repository.CategoryRepository;
import com.example.pib2.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    public List<Category> getCategoriesByBusiness(Business business) {
        return categoryRepository.findByBusiness(business);
    } 

    public Optional<Category>  getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public boolean delete(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Category createCategory(String name, Business business) {
        Category category = new Category();
        category.setName(name);
        category.setBusiness(business);
        return categoryRepository.save(category);
    }
}
