package com.example.pib2.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.Category;
import com.example.pib2.model.entity.Product;
import com.example.pib2.model.entity.Sale;
import com.example.pib2.model.entity.Subscription;
import com.example.pib2.model.entity.User;
import com.example.pib2.model.entity.UserRole;
import com.example.pib2.repository.BusinessRepository;
import com.example.pib2.repository.CategoryRepository;
import com.example.pib2.repository.ProductRepository;
import com.example.pib2.repository.SaleRepository;
import com.example.pib2.repository.SubscriptionRepository;
import com.example.pib2.repository.UserRepository;

@Service
public class SeederService {

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    public User createUser(String name, String email, String password, UserRole role, Business business) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(password));
        u.setRole(role);
        u.setBusiness(business);
        return userRepository.save(u);
    }

    public Business createBusiness(String name, String address) {
        Business b = new Business();
        b.setName(name);
        b.setAddress(address);

        return businessRepository.save(b);
    }

    public Subscription createSubscription(String planName, Business business) {
        Subscription s = new Subscription();
        s.setPlanName(planName);
        s.setBusiness(business);

        return subscriptionRepository.save(s);
    }

    public Category createCategory(String name, String description, Business business) {
        Category c = new Category();
        c.setName(name);
        c.setDescription(description);
        c.setActive(true);
        c.setBusiness(business);
        return categoryRepository.save(c);
    }

    public Product createProduct(String name, String description, Double price, Category category, Business business) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setActive(true);
        p.setStock(0);
        p.setBusiness(business);
        p.setCategory(category);
        return productRepository.save(p);
    }

    public Sale createSale(LocalDateTime date, int quantity, double price, Product product, Business business) {
        Sale s = new Sale();
        s.setDate(date);
        s.setQuantity(quantity);
        s.setPrice(price);
        s.setProduct(product);
        s.setBusiness(business);
        return saleRepository.save(s);
    }

}
