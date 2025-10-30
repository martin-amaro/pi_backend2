package com.example.pib2.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.pib2.model.dto.ProductRequestDTO;
import com.example.pib2.model.dto.ProductResponseDTO;
import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.Product;
import com.example.pib2.model.entity.User;
import com.example.pib2.model.entity.UserRole;
import com.example.pib2.repository.ProductRepository;
import com.example.pib2.service.ImageService;
// import com.example.pib2.repository.UserRepository;
// import com.example.pib2.service.BusinessService;
import com.example.pib2.service.ProductService;
import com.example.pib2.service.impl.ProductServiceImpl;
import com.example.pib2.util.AuthUtils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
@Tag(name = "gestion de  productos", description = "Endpoints para crear,consultar, actualizar y eliminar productos . Autenticacion Obligatoria")
@SecurityRequirement(name = "basicAuth")
public class ProductController {

    @Autowired
    private ImageService imageService;

    // @Autowired
    // private BusinessService businessService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        try {
            User user = AuthUtils.getCurrentUser();
            Business business = user.getBusiness();

            List<Product> products = productRepository.findByBusiness(business);
            return ResponseEntity.ok(products);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {

        try {
            User user = AuthUtils.getCurrentUser();
            Business business = user.getBusiness();

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Product> productPage = productService.searchProducts(business, query, pageable);

            Map<String, Object> result = Map.of(
                    "content", productPage.getContent(),
                    "currentPage", productPage.getNumber() + 1,
                    "totalPages", productPage.getTotalPages(),
                    "totalItems", productPage.getTotalElements(),
                    "query", query);

            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestPart("product") @Valid ProductRequestDTO request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        try {
            User user = AuthUtils.getCurrentUser();
            AuthUtils.checkPermissions(user, List.of(UserRole.ADMIN));

            Business business = user.getBusiness();
            if (business == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "No se encontró la empresa asociada al usuario"));
            }

            Product product = productService.createProduct(request, business);

            // Si hay imágenes, subirlas
            if (images != null && !images.isEmpty()) {
                for (MultipartFile file : images) {
                    Map<String, Object> uploadResult = imageService.uploadFile(file);

                    if ((boolean) uploadResult.get("success")) {
                        String url = (String) uploadResult.get("uploaded_url");
                        imageService.saveImageForProduct(url, product);
                    } else {
                        System.out.println("Error subiendo imagen: " + uploadResult.get("error"));
                    }
                }
            }

            // Convertir a DTO antes de responder
            ProductResponseDTO response = ((ProductServiceImpl) productService).mapToDTO(product);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }

    }

}
