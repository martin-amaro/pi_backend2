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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/categories")
@Tag(name = "Categorías", description = "Operaciones relacionadas con la gestión de categorías de negocios")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryRepository categoryRepository;

    @Operation(
        summary = "Obtener todas las categorías",
        description = "Devuelve una lista de todas las categorías asociadas al negocio actual del usuario autenticado.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "401", description = "No autorizado o token inválido",
                content = @Content(mediaType = "application/json"))
        }
    )
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

    @Operation(
        summary = "Crear una nueva categoría",
        description = "Crea una nueva categoría en el negocio actual. Solo los administradores pueden realizar esta acción.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Categoría creada correctamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "400", description = "Error en los datos enviados",
                content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "No autorizado o token inválido",
                content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes",
                content = @Content(mediaType = "application/json"))
        }
    )
    @PostMapping
    public ResponseEntity<?> createCategory(
        @Parameter(description = "Datos para crear una nueva categoría", required = true)
        @Valid @RequestBody CategoryRequestDTO request
    ) {
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

    @Operation(
        summary = "Eliminar una categoría",
        description = "Elimina una categoría por su ID. Solo los administradores del negocio propietario pueden eliminar categorías.",
        responses = {
            @ApiResponse(responseCode = "204", description = "Categoría eliminada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado o token inválido",
                content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "El usuario no tiene permiso para eliminar la categoría",
                content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada",
                content = @Content(mediaType = "application/json"))
        }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(
        @Parameter(description = "ID de la categoría a eliminar", required = true, example = "1")
        @Valid @PathVariable Long id
    ) {
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
