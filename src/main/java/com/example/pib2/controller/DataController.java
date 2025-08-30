package com.example.pib2.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.Category;
import com.example.pib2.model.entity.Product;
import com.example.pib2.model.entity.User;

import com.example.pib2.repository.BusinessRepository;
import com.example.pib2.repository.CategoryRepository;
import com.example.pib2.repository.ProductRepository;
import com.example.pib2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/seed")
public class DataController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    DataController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String injectData() {

        // Business
        Business business = new Business();
        business.setName("Tienda Central");
        business.setAddress("Calle 123");
        businessRepository.save(business);

        // Categorias
        Category bebidas = new Category();
        bebidas.setName("Bebidas");
        categoryRepository.save(bebidas);

        Category aseo = new Category();
        aseo.setName("Aseo");
        categoryRepository.save(aseo);

        // Crear productos
        Product coca = new Product();
        coca.setName("Coca Cola");
        coca.setDescription("Gaseosa de 1.5L");
        coca.setPrice(4500.0);
        coca.setStock(50);
        coca.setActive(true);
        coca.setCategory(bebidas);
        coca.setBusiness(business);
        productRepository.save(coca);

        Product escoba = new Product();
        escoba.setName("Escoba");
        escoba.setDescription("Escoba de cerdas suaves");
        escoba.setPrice(8000.0);
        escoba.setStock(20);
        escoba.setActive(true);
        escoba.setCategory(aseo);
        escoba.setBusiness(business);
        productRepository.save(escoba);

        // User
        User user1 = new User();
        user1.setName("Martin Amaro");
        user1.setEmail("martin@gmail.com");
        user1.setPassword("123456");
        user1.setBusiness(business);
        userRepository.save(user1);

        return "Datos de prueba insertados correctamente.";
    }

}
