package com.example.pib2.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.Sale;
import com.example.pib2.model.entity.User;
import com.example.pib2.model.entity.UserRole;
import com.example.pib2.repository.SaleRepository;
import com.example.pib2.service.SaleService;
import com.example.pib2.util.AuthUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/sales")
@Tag(name = "Ventas", description = "Operaciones relacionadas con las ventas de un negocio")
public class SalasController {

    @Autowired
    SaleService saleService;

    @Autowired
    SaleRepository saleRepository;

    @Operation(
        summary = "Obtiene todas las ventas del negocio actual",
        description = """
            Devuelve una lista de ventas asociadas al negocio autenticado.  
            Solo los usuarios con rol ADMIN pueden acceder a este recurso.  
            Si el usuario no tiene permisos o el negocio no es válido, retorna un error 401.
            """,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de ventas obtenida correctamente",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Sale.class)),
                    examples = @ExampleObject(
                        name = "Ejemplo de lista de ventas",
                        value = """
                        [
                          {
                            "id": 1,
                            "amount": 45000,
                            "date": "2025-10-31T12:30:00",
                            "business": {
                              "id": 5,
                              "name": "Tech Solutions"
                            }
                          },
                          {
                            "id": 2,
                            "amount": 70000,
                            "date": "2025-11-01T09:00:00",
                            "business": {
                              "id": 5,
                              "name": "Tech Solutions"
                            }
                          }
                        ]
                        """
                    )
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "No autorizado. El usuario no tiene permisos o el token es inválido.",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                        type = "object",
                        example = "{\"error\": \"Usuario no autorizado o sin permisos suficientes.\"}"
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
    @GetMapping
    public ResponseEntity<?> getAllSales() {
        try {
            User user = AuthUtils.getCurrentUser();
            Business business = AuthUtils.getCurrentBusiness();
            AuthUtils.checkPermissions(user, List.of(UserRole.ADMIN));

            List<Sale> sales = saleRepository.findByBusiness(business);
            return ResponseEntity.ok(sales);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
