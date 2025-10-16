package com.example.pib2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pib2.model.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
    Optional<Subscription> findFirstByBusiness_IdAndStatus(Long businessId, String status);
    Optional<Subscription> findByStripeSessionId(String stripeSessionId);
}
