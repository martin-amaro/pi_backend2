package com.example.pib2.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pib2.model.dto.ImageUploadDTO;
import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.Image;
import com.example.pib2.repository.ImageRepository;
import com.example.pib2.service.ImageService;
import com.example.pib2.util.AuthUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/images")
@Tag(name = "Imágenes", description = "Operaciones para subir y administrar imágenes")
public class ImageController {

    @Autowired
    ImageService imageService;

    @Autowired
    ImageRepository imageRepository;

    @Operation(
        summary = "Sube una imagen asociada al negocio autenticado",
        description = """
            Este endpoint permite crear un registro de imagen en la base de datos
            vinculado al negocio actualmente autenticado mediante `AuthUtils.getCurrentBusiness()`.
            Retorna un mensaje de éxito o un error de autorización.
            """,
        requestBody = @RequestBody(
            required = true,
            description = "Datos de la imagen a subir",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ImageUploadDTO.class),
                examples = @ExampleObject(
                    name = "Ejemplo de carga de imagen",
                    value = """
                    {
                      "filename": "foto_perfil.jpg",
                      "contentType": "image/jpeg",
                      "size": 204800,
                      "data": "iVBORw0KGgoAAAANSUhEUgAA..."
                    }
                    """
                )
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Imagen subida exitosamente",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "string"),
                    examples = @ExampleObject(value = "Image uploaded successfully")
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "No autorizado. No se encontró un negocio válido en el contexto.",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        type = "object",
                        example = "{\"error\": \"Usuario no autorizado o token inválido.\"}"
                    )
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno en el servidor.",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        type = "object",
                        example = "{\"error\": \"Error interno del servidor.\"}"
                    )
                )
            )
        }
    )
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@org.springframework.web.bind.annotation.RequestBody ImageUploadDTO dto) {
        try {
            Business business = AuthUtils.getCurrentBusiness();
            System.out.println(business.getName());
            System.out.println(business.getId());

            Image image = new Image();
            image.setBusiness(business);
            image.setUrl("https://example.com/image.jpg");

            imageRepository.save(image);

            return ResponseEntity.ok("Image uploaded successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
