package com.example.pib2.repository;

import java.util.List;
import java.util.Optional;

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
    Optional<Business> findByName(String businessName);


    @Query("""
        SELECT p FROM Product p
        WHERE p.business = :business
        AND (
            :categoryId IS NULL
            OR (p.category IS NOT NULL AND p.category.id = :categoryId)
        )
        AND (
            :query = ''
            OR LOWER(p.name) LIKE %:query%
            OR LOWER(p.description) LIKE %:query%
        )
        ORDER BY p.createdAt DESC
    """)
    Page<Product> searchProductsDynamic(
            @Param("business") Business business,
            @Param("query") String query,
            @Param("categoryId") Long categoryId,
            Pageable pageable);
}
