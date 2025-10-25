package com.example.pib2.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pib2.model.dto.category.CategoryRequestDTO;
import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.Category;
import com.example.pib2.model.entity.User;
import com.example.pib2.model.entity.UserRole;
import com.example.pib2.repository.CategoryRepository;
import com.example.pib2.service.CategoryService;
import com.example.pib2.util.AuthUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        try {
            Business business = AuthUtils.getCurrentBusiness();
            List<Category> categories = categoryService.getCategoriesByBusiness(business);
            return ResponseEntity.ok(categories);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequestDTO request) {
        System.out.println("Creating category with name: " + request.getName());
        try {
            User user = AuthUtils.getCurrentUser();
            Business business = AuthUtils.getCurrentBusiness();
            AuthUtils.checkPermissions(user, List.of(UserRole.ADMIN));

            Category category = categoryService.createCategory(request.getName(), business);
            return ResponseEntity.status(HttpStatus.CREATED).body(category);

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            System.out.println(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@Valid @PathVariable Long id) {
        try {
            User user = AuthUtils.getCurrentUser();
            Business business = AuthUtils.getCurrentBusiness();
            AuthUtils.checkPermissions(user, List.of(UserRole.ADMIN));


            Optional<Category> categoryOpt = categoryService.getCategoryById(id);
            if (categoryOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Categoría no encontrada"));
            }

            Category targetCategory = categoryOpt.get();

            if (business.getId() != targetCategory.getBusiness().getId()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No tiene permiso para eliminar esta categoría"));
            }

            boolean deleted = categoryService.delete(targetCategory.getId());
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Categoria no encontrada"));
            }

            return ResponseEntity.noContent().build();


        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
