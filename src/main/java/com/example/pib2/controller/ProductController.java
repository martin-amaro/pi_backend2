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
import org.springframework.web.bind.annotation.*;

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
import com.example.pib2.service.ProductService;
import com.example.pib2.service.impl.ProductServiceImpl;
import com.example.pib2.util.AuthUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Gestión de productos", description = "Endpoints para crear, consultar, buscar y actualizar productos. Autenticación obligatoria.")
@SecurityRequirement(name = "basicAuth")
public class ProductController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    // -----------------------------------------------------
    // GET /api/products
    // -----------------------------------------------------
    @Operation(summary = "Obtener todos los productos", description = "Devuelve la lista completa de productos pertenecientes al negocio asociado al usuario autenticado.", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida correctamente", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponseDTO.class)))),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado o sin negocio asociado", content = @Content(schema = @Schema(example = "{\"error\": \"Token inválido o expirado\"}")))
    })
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

    // -----------------------------------------------------
    // GET /api/products/search
    // -----------------------------------------------------
    @Operation(summary = "Buscar productos", description = "Permite buscar productos dentro del negocio autenticado filtrando por texto, categoría o paginación.", parameters = {
            @Parameter(name = "query", description = "Texto a buscar en el nombre o descripción del producto", example = "camisa"),
            @Parameter(name = "category", description = "ID de la categoría a filtrar", example = "3"),
            @Parameter(name = "page", description = "Número de página (1 por defecto)", example = "1"),
            @Parameter(name = "size", description = "Cantidad de elementos por página (10 por defecto)", example = "10")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "Resultados de búsqueda", content = @Content(schema = @Schema(example = """
                    {
                      "content": [ { "id": 1, "name": "Camisa Azul", "price": 50000 } ],
                      "currentPage": 1,
                      "totalPages": 3,
                      "totalItems": 25,
                      "query": "camisa"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado", content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {

        try {
            Business business = AuthUtils.getCurrentBusiness();

            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Product> productPage = productService.searchProducts(business, query, category, pageable);

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

    // -----------------------------------------------------
    // POST /api/products
    // -----------------------------------------------------
    @Operation(summary = "Crear un nuevo producto", description = "Crea un producto nuevo asociado al negocio del usuario autenticado. Se pueden subir imágenes opcionales.", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del producto y sus imágenes opcionales", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = ProductRequestDTO.class))), responses = {
            @ApiResponse(responseCode = "201", description = "Producto creado correctamente", content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Negocio no encontrado o datos inválidos", content = @Content(schema = @Schema(example = "{\"error\": \"No se encontró la empresa asociada al usuario\"}"))),
            @ApiResponse(responseCode = "401", description = "Usuario no autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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

            ProductResponseDTO response = ((ProductServiceImpl) productService).mapToDTO(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // -----------------------------------------------------
    // PUT /api/products/{id}
    // -----------------------------------------------------
    @Operation(summary = "Actualizar un producto existente", description = "Permite modificar los datos y las imágenes de un producto existente.", parameters = {
            @Parameter(name = "id", description = "ID del producto a actualizar", required = true, example = "1")
    }, requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del producto actualizados, imágenes nuevas y/o imágenes eliminadas.", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = ProductRequestDTO.class))), responses = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente", content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content(schema = @Schema(example = "{\"error\": \"Producto no encontrado\"}"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestPart("product") @Valid ProductRequestDTO request,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages) {

        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            Business business = product.getBusiness();
            productService.updateProduct(product, request, business);

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

            if (request.getRemovedImages() != null) {
                for (String url : request.getRemovedImages()) {
                    imageService.deleteImageByUrl(url, product);
                }
            }

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

    @PostMapping("/delete")
    public ResponseEntity<?> deleteProducts(@RequestBody Map<String, List<Long>> body) {
        try {
            List<Long> ids = body.get("ids");

            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "El array 'ids' no puede estar vacío."));
            }

            User user = AuthUtils.getCurrentUser();
            AuthUtils.checkPermissions(user, List.of(UserRole.ADMIN));

            Map<String, Object> result = productService.deleteMultiple(ids);

            return ResponseEntity.ok(result);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error interno del servidor."));
        }

    }

}
