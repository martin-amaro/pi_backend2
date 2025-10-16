package com.example.pib2.model.dto;

import java.util.List;

public class InvoiceRequest {

    private Long userId;
    private List<InvoiceItemRequest> itemRequests;

    // --- Getters y Setters ---
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<InvoiceItemRequest> getItemRequests() {
        return itemRequests;
    }

    public void setItemRequests(List<InvoiceItemRequest> itemRequests) {
        this.itemRequests = itemRequests;
    }
}