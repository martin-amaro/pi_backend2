package com.example.pib2.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.pib2.model.dto.SubscriptionDTO;
import com.example.pib2.model.entity.Business;
import com.example.pib2.model.entity.Subscription;
import com.example.pib2.model.entity.User;
import com.example.pib2.repository.BusinessRepository;
import com.example.pib2.repository.SubscriptionRepository;
import com.example.pib2.service.SubscriptionService;
import com.example.pib2.util.AuthUtils;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private BusinessRepository businessRepository;

    @PostMapping("/activate")
    public ResponseEntity<?> activateSubscription(@RequestBody SubscriptionDTO dto) {
        Business business = businessRepository.findById(Long.parseLong(dto.getBusinessId()))
                .orElseThrow(() -> new RuntimeException("Business not found"));

    //     Subscription subscription = subscriptionService.findByStripeSubscriptionId(stripeSubscriptionId)
    // .orElse(new Subscription());
    
        Subscription subscription = new Subscription();
        subscription.setBusiness(business);
        subscription.setStripeCustomerId(dto.getStripeCustomerId());
        subscription.setStripeSubscriptionId(dto.getStripeSubscriptionId());
        subscription.setStripeSessionId(dto.getStripeSessionId());
        subscription.setPlanName(dto.getPlanName());
        subscription.setStatus("ACTIVE");
        subscription.setStartDate(LocalDateTime.now());

        subscriptionRepository.save(subscription);

        business.setSubscription(subscription);
        businessRepository.save(business);

        return ResponseEntity.ok("Subscription activated");
    }

    @PostMapping("/status")
    public ResponseEntity<?> checkSubscription() {
        
        try {
            User user = AuthUtils.getCurrentUser();
            Business business = user.getBusiness();

            // Buscar la subscripción activa
            Subscription subscription = null;

            if (business != null) {
                subscription = subscriptionService.findActiveByBusinessId(business.getId())
                        .orElse(null);
            }

            // Preparar datos de suscripción
            String planName = (subscription != null) ? subscription.getPlanName() : "free";
            String status = (subscription != null) ? subscription.getStatus() : "INACTIVE";

            return ResponseEntity.ok(Map.of(
                    "planName", planName,
                    "status", status));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/verify-session")
    public ResponseEntity<?> checkSubscriptionSession(@RequestParam String session_id) {
        Optional<Subscription> subscription = subscriptionService.findByStripeSessionId(session_id);

        if (subscription.isPresent() && subscription.get().getStatus().equals("ACTIVE")) {
            return ResponseEntity.ok(Map.of("verified", true));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "invalid session"));
    }

}
