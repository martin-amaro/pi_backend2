package com.example.pib2.model.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "sales")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;

    private int quantity;

    private double price;

    @ManyToOne
    @JsonIgnore
    private Product product;

    @ManyToOne
    @JsonIgnore
    private Business business;
}
