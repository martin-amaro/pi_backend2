package com.example.pib2.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByBusiness(Business business);

    Page<Product> findByBusiness(Business business, Pageable pageable);

    @Query("""
        SELECT p FROM Product p
        WHERE p.business = :business
          AND (LOWER(p.name) LIKE %:query%
               OR LOWER(p.description) LIKE %:query%)
    """)
    Page<Product> searchByBusinessAndQuery(@Param("business") Business business,
                                           @Param("query") String query,
                                           Pageable pageable);
}
