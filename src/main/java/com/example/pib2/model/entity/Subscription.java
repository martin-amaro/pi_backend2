package com.example.pib2.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Subscription {
    @Id
    private String id = java.util.UUID.randomUUID().toString();

    private String stripeSubscriptionId;
    private String stripeCustomerId;

    private String stripeSessionId;
    
    private String planName;
    private String status; // ACTIVE, CANCELED, INCOMPLETE, etc.

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @OneToOne
    @JoinColumn(name = "business_id")
    private Business business;
}
