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

@RestController
@RequestMapping("/sales")
public class SalasController {
    
    @Autowired
    SaleService saleService;

    @Autowired
    SaleRepository saleRepository;

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
