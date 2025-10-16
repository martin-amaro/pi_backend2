package com.example.pib2.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.pib2.model.entity.Subscription;
import com.example.pib2.repository.SubscriptionRepository;
import com.example.pib2.service.SubscriptionService;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Override
    public Optional<Subscription> findActiveByBusinessId(Long businessId) {
        return subscriptionRepository.findFirstByBusiness_IdAndStatus(businessId, "ACTIVE");
    }

    @Override
    public Optional<Subscription> findByStripeSessionId(String sessionId) {
        return subscriptionRepository.findByStripeSessionId(sessionId);
    }

}
