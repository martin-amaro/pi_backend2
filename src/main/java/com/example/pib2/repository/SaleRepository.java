package com.example.pib2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.Sale;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByBusiness(Business business);
}
