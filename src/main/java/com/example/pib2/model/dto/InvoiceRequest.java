package com.example.pib2.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class InvoiceRequest {
    private Long userId;
    private List<InvoiceItemRequest> itemRequests;
}
