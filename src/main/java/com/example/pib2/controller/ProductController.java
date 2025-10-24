package com.example.pib2.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pib2.model.dto.ProductRequestDTO;
import com.example.pib2.model.dto.ProductResponseDTO;
import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.Product;
import com.example.pib2.model.entity.User;
import com.example.pib2.model.entity.UserRole;
// import com.example.pib2.repository.UserRepository;
// import com.example.pib2.service.BusinessService;
import com.example.pib2.service.ProductService;
import com.example.pib2.service.impl.ProductServiceImpl;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
@Tag(name = "gestion de  productos", description = "Endpoints para crear,consultar, actualizar y eliminar productos . Autenticacion Obligatoria")
@SecurityRequirement(name = "basicAuth")
public class ProductController {

    // @Autowired
    // private UserRepository userRepository;

    // @Autowired
    // private BusinessService businessService;

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequestDTO request,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No autenticado"));
        }

        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRole() != UserRole.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Solo administradores pueden acceder"));

        }

        Business business = currentUser.getBusiness();

        if (business == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "No se encontró la empresa asociada al usuario"));
        }

        Product product = productService.createProduct(request, business);

        // Convertir a DTO antes de responder
        ProductResponseDTO response = ((ProductServiceImpl) productService).mapToDTO(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
