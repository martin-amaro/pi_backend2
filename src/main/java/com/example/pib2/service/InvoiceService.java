package com.example.pib2.service;

import java.util.List;

import com.example.pib2.model.dto.InvoiceRequest;
import com.example.pib2.model.entity.Invoice;

public interface InvoiceService {

    Invoice createInvoice(InvoiceRequest request);

    Invoice getInvoiceById(Long id);

    List<Invoice> getAllInvoices();

}