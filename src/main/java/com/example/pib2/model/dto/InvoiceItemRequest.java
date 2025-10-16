package com.example.pib2.model.dto;

public class InvoiceItemRequest {

    private Long productId;
    private Integer quantity;

    // --- Getters y Setters ---
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}