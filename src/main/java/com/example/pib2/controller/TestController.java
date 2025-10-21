package com.example.pib2.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pib2.model.entity.User;
import com.example.pib2.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/test")
@Tag(name = "Test-Controller", description = "Endpoints de utilidad para verificar que la API está en línea y sea accesible,este tambien se prueba para verificar el funcionamiento del controlador.")
public class TestController {

    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "Verificar estado básico", description = "Retorna un objeto simple para confirmar que el controlador está activo.", responses = {
            @ApiResponse(responseCode = "200", description = "Test exitoso."),
    })
    public Map<String, String> test() {
        return Map.of("test", "example");
    }

    @GetMapping("/users")
    @Operation(summary = "Obtener todos los usuarios (Prueba de BD)", description = "Endpoint de prueba para verificar la conexión a la base de datos y listar todos los usuarios. **¡Solo para desarrollo/pruebas!**", responses = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios devuelta."),
            @ApiResponse(responseCode = "500", description = "Error interno (ej. fallo de conexión a BD)."),
    })
    public List<User> getAllUsers() {
        return userService.getAll();
    }
}