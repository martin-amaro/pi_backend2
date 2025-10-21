package com.example.pib2.model.dto;

import lombok.Data;

@Data
public class InvoiceItemRequest {

    private Long productId;
    private Integer quantity;

}
