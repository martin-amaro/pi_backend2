package com.example.pib2.model.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubscriptionDTO {
    @NotBlank
    private String businessId;

    @NotBlank
    private String stripeSubscriptionId;
    private String stripeCustomerId;
    private String stripeSessionId;
    private String planName;
    private String status;
    private LocalDateTime startDate;      // Fecha de inicio (opcional)
    private LocalDateTime endDate;
}
