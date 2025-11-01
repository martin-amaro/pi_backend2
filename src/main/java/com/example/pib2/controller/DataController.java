package com.example.pib2.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.Category;
import com.example.pib2.model.entity.Product;
import com.example.pib2.model.entity.UserRole;
import com.example.pib2.service.SeederService;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/seed")
@Tag(name = "Seeder", description = "Inserta datos de prueba en la base de datos para desarrollo o pruebas.")
@SecurityRequirement(name = "bearerAuth")
public class DataController {

    @Autowired
    private SeederService seederService;

    @Operation(
        summary = "Inyectar datos de prueba",
        description = """
            Este endpoint crea una base de datos inicial con:
            - Una empresa (Business)
            - Usuarios (Admin y User)
            - Categorías predefinidas
            - Productos asociados a cada categoría
            - Ventas de ejemplo con fechas aleatorias recientes
            
            Solo debe ejecutarse en entornos de desarrollo o testing.
            """,
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Los datos de prueba fueron insertados correctamente.",
                content = @Content(mediaType = "text/plain", schema = @Schema(example = "Datos de prueba insertados correctamente."))
            ),
            @ApiResponse(
                responseCode = "401",
                description = "No autorizado. El usuario no tiene permisos para ejecutar el seeder.",
                content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno al insertar los datos de prueba.",
                content = @Content(mediaType = "application/json")
            )
        }
    )
    @GetMapping
    public String injectData() {

        // Business
        Business business = seederService.createBusiness("Insignia Indumentaria", "Av 44. Diagonal 66 - 58");
        seederService.createSubscription("pro", business);

        // Users
        seederService.createUser("Juan Restrepo", "admin@insignia.com", "ADMIN2025", UserRole.ADMIN, business);
        seederService.createUser("Miguel Gonzáles", "miguel@insignia.com", "123456", UserRole.USER, business);

        // Categories
        Category category1 = seederService.createCategory("Calzado", "Zapatos y botas", business);
        Category category2 = seederService.createCategory("Camisas", "Camisas, sudaderas, busos", business);
        Category category3 = seederService.createCategory("Sombreros y gorros", "Sombrero, gorros, bandanas", business);

        // Products - Calzado
        Product product1 = seederService.createProduct(
            "Nike Air Max",
            "Descubre la combinación perfecta de **estilo icónico y comodidad superior** con las Nike Air Max. Su revolucionaria **unidad Air visible** te ofrece una amortiguación excepcional que absorbe el impacto.",
            350000.0, category1, business);

        Product product2 = seederService.createProduct(
            "Adidas Ultraboost",
            "Las Adidas Ultraboost fusionan tecnología y diseño moderno. Su suela **Boost** proporciona un retorno de energía impresionante en cada paso.",
            420000.0, category1, business);

        Product product3 = seederService.createProduct(
            "Botas Timberland Classic",
            "Resistentes al agua y con suela antideslizante, las **Timberland Classic** son ideales para aventuras urbanas o al aire libre.",
            480000.0, category1, business);

        Product product4 = seederService.createProduct(
            "Converse Chuck Taylor",
            "Un ícono atemporal. Las **Chuck Taylor All Star** se reinventan para adaptarse a cualquier estilo sin perder su esencia.",
            270000.0, category1, business);

        // Products - Camisas
        Product product5 = seederService.createProduct(
            "Camisa Oxford Azul",
            "La clásica **camisa Oxford** de algodón ofrece una textura suave y un acabado elegante para cualquier ocasión formal o casual.",
            180000.0, category2, business);

        Product product6 = seederService.createProduct(
            "Sudadera Essentials Negra",
            "Sudadera **oversize** con capucha, confeccionada en algodón premium. Cómoda, versátil y con un diseño minimalista.",
            220000.0, category2, business);

        Product product7 = seederService.createProduct(
            "Buso Deportivo Nike Dri-FIT",
            "Diseñado con tecnología **Dri-FIT**, este buso mantiene el cuerpo seco durante entrenamientos intensos.",
            190000.0, category2, business);

        Product product8 = seederService.createProduct(
            "Camisa de Lino Blanca",
            "Fresca y ligera, la **camisa de lino blanca** es la elección ideal para climas cálidos y eventos relajados.",
            200000.0, category2, business);

        // Products - Sombreros y gorros
        Product product9 = seederService.createProduct(
            "Sombrero Fedora",
            "El **Fedora clásico** aporta un toque de elegancia y distinción a cualquier look casual o formal.",
            150000.0, category3, business);

        Product product10 = seederService.createProduct(
            "Gorra New Era 9FIFTY",
            "La **New Era 9FIFTY** combina un diseño moderno con materiales duraderos. Perfecta para el día a día.",
            130000.0, category3, business);

        Product product11 = seederService.createProduct(
            "Gorro de Lana Unisex",
            "Te mantiene abrigado con estilo. El **gorro de lana unisex** es ideal para días fríos y atuendos casuales.",
            90000.0, category3, business);

        Product product12 = seederService.createProduct(
            "Bandana Estampada",
            "Versátil y con **diseños vibrantes**, la bandana estampada puede usarse como accesorio para la cabeza o el cuello.",
            60000.0, category3, business);

        // Sales
        seederService.createSale(LocalDateTime.now().minusDays(1), 2, 350000.0, product1, business);
        seederService.createSale(LocalDateTime.now().minusDays(3), 1, 420000.0, product2, business);
        seederService.createSale(LocalDateTime.now().minusDays(5), 3, 480000.0, product3, business);
        seederService.createSale(LocalDateTime.now().minusDays(2), 1, 270000.0, product4, business);

        seederService.createSale(LocalDateTime.now().minusDays(7), 4, 180000.0, product5, business);
        seederService.createSale(LocalDateTime.now().minusDays(4), 2, 220000.0, product6, business);
        seederService.createSale(LocalDateTime.now().minusDays(1), 3, 190000.0, product7, business);
        seederService.createSale(LocalDateTime.now().minusDays(6), 1, 200000.0, product8, business);

        seederService.createSale(LocalDateTime.now().minusDays(2), 1, 150000.0, product9, business);
        seederService.createSale(LocalDateTime.now().minusDays(3), 2, 130000.0, product10, business);
        seederService.createSale(LocalDateTime.now().minusDays(5), 5, 90000.0, product11, business);
        seederService.createSale(LocalDateTime.now().minusDays(1), 6, 60000.0, product12, business);

        return "Datos de prueba insertados correctamente.";
    }
}
