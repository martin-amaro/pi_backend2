package com.example.pib2.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/test")
@Tag(name = "Test-Controller", description = "Endpoints de utilidad para verificar que la API está en línea y sea accesible,este tambien se prueba para verificar el funcionamiento del controlador.")
public class TestController {

    @GetMapping
    public Map<String, String> test() {

        return Map.of("test", "example");

    }

}
