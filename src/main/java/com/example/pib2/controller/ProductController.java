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
import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.pib2.model.dto.ProductRequestDTO;
import com.example.pib2.model.dto.ProductResponseDTO;
import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.Image;
import com.example.pib2.model.entity.Product;
import com.example.pib2.model.entity.User;
import com.example.pib2.model.entity.UserRole;
import com.example.pib2.repository.ImageRepository;
import com.example.pib2.repository.ProductRepository;
import com.example.pib2.service.ImageService;
// import com.example.pib2.repository.UserRepository;
// import com.example.pib2.service.BusinessService;
import com.example.pib2.service.ProductService;
import com.example.pib2.service.impl.ProductServiceImpl;
import com.example.pib2.util.AuthUtils;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageRepository imageRepository;

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

            List<ProductResponseDTO> response = products.stream()
                    .map(productService::mapToDTO)
                    .toList();

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {

        try {

            System.out.println("query=" + query);
            System.out.println("categoryId=" + category);

            // User user = AuthUtils.getCurrentUser();

            Business business = AuthUtils.getCurrentBusiness();

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Product> productPage = productService.searchProducts(business, query, category, pageable);

            // Mapea los productos a DTOs
            List<ProductResponseDTO> content = productPage.getContent()
                    .stream()
                    .map(productService::mapToDTO)
                    .toList();

            Map<String, Object> result = Map.of(
                    "content", content,
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
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages) {
        try {
            User user = AuthUtils.getCurrentUser();
            AuthUtils.checkPermissions(user, List.of(UserRole.ADMIN));

            Business business = user.getBusiness();
            if (business == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "No se encontró la empresa asociada al usuario"));
            }

            Product product = productService.createProduct(request, business);

            int thumbIndex = request.getThumbIndex();

            // Si hay imágenes, subirlas
            if (newImages != null && !newImages.isEmpty()) {
                for (int i = 0; i < newImages.size(); i++) {
                    MultipartFile file = newImages.get(i);
                    Map<String, Object> uploadResult = imageService.uploadFile(file);

                    if ((boolean) uploadResult.get("success")) {
                        String url = (String) uploadResult.get("uploaded_url");
                        imageService.saveImageForProduct(url, (String) uploadResult.get("fileId"), product,
                                i == thumbIndex);
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

    // Actualizar producto
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") @Valid ProductRequestDTO request,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages) {

        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            Business business = product.getBusiness();
            // Actualizar los datos del producto

            productService.updateProduct(product, request, business);

            // Subir nuevas imágenes (si las hay)
            int thumbIndex = request.getThumbIndex();

            if (newImages != null && !newImages.isEmpty()) {
                for (int i = 0; i < newImages.size(); i++) {
                    MultipartFile file = newImages.get(i);
                    Map<String, Object> uploadResult = imageService.uploadFile(file);

                    if ((boolean) uploadResult.get("success")) {
                        String url = (String) uploadResult.get("uploaded_url");
                        imageService.saveImageForProduct(url, (String) uploadResult.get("fileId"), product,
                                i == thumbIndex);
                    }
                }
            }

            // Eliminar imágenes solo si el usuario lo pidió
            if (request.getRemovedImages() != null) {
                System.out.println("La calle ando");
                for (String url : request.getRemovedImages()) {
                    imageService.deleteImageByUrl(url, product);
                }
            }

            // Actualizar el thumbnail (main) de imágenes existentes
            // Si thumbIndex apunta a una imagen vieja, no se había actualizado arriba
            if (thumbIndex >= 0) {
                var images = imageRepository.findByProduct(product);

                for (int i = 0; i < images.size(); i++) {
                    Image img = images.get(i);
                    img.setMain(i == thumbIndex);
                }

                imageRepository.saveAll(images);
            }

            productRepository.save(product);
            return ResponseEntity.ok(((ProductServiceImpl) productService).mapToDTO(product));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
