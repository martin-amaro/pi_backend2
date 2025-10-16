package com.example.pib2.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.pib2.model.dto.InvoiceRequest;
import com.example.pib2.model.entity.Invoice;
import com.example.pib2.model.entity.InvoiceItem;
import com.example.pib2.repository.InvoiceRepository;
import com.example.pib2.repository.ProductRepository;
import com.example.pib2.repository.UserRepository;
import com.example.pib2.service.InvoiceService;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository; // Para obtener el precio del producto

    // Constructor con inyección de dependencias
    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, UserRepository userRepository,
            ProductRepository productRepository) {
        this.invoiceRepository = invoiceRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Invoice createInvoice(InvoiceRequest request) {
        // 1. Obtener el cliente (User)
        // Lógica de error si el usuario no existe
        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Invoice invoice = new Invoice();
        invoice.setUser(user);

        double total = 0.0;

        // Crear los items de la factura y calcular el total
        List<InvoiceItem> items = request.getItemRequests().stream()
                .map(itemReq -> {
                    // Obtener el producto (para asegurar el precio y existencia)
                    var product = productRepository.findById(itemReq.getProductId())
                            .orElseThrow(
                                    () -> new RuntimeException("Producto no encontrado: " + itemReq.getProductId()));

                    InvoiceItem item = new InvoiceItem();
                    item.setInvoice(invoice);
                    item.setProduct(product);
                    item.setQuantity(itemReq.getQuantity());

                    Double price = product.getPrice();
                    Double subtotal = price * itemReq.getQuantity();

                    item.setUnitPrice(price);
                    item.setSubtotal(subtotal);

                    return item;
                })
                .collect(Collectors.toList());

        total = items.stream().mapToDouble(InvoiceItem::getSubtotal).sum();

        invoice.setItems(items);
        invoice.setTotalAmount(total);

        // Guardar la factura (los items se guardan en cascada)
        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }
}