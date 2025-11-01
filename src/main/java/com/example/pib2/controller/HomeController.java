package com.example.pib2.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/")
@Tag(name = "Home-Controller", description = "Endpoints de bienvenida y punto de entrada principal a la API. Muestra el estado del servicio.")
public class HomeController {
    @GetMapping
    @Operation(summary = "Mensaje de bienvenida", description = "Punto de entrada principal para verificar que la API está operativa.", responses = {
            @ApiResponse(responseCode = "200", description = "API operativa. Mensaje de estado devuelto."),
    })
    public String home() {
        return "✅ Stocker Backend está corriendo";
    }
}
