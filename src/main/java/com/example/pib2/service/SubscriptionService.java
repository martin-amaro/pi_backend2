package com.example.pib2.service;

import java.util.Optional;

import com.example.pib2.model.entity.Subscription;

public interface SubscriptionService {
    public Optional<Subscription> findActiveByBusinessId(Long businessId);
    public Optional<Subscription> findByStripeSessionId(String sessionId);
}
