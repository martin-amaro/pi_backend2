package com.example.pib2.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pib2.model.dto.InvoiceRequest;
import com.example.pib2.model.entity.Invoice;
import com.example.pib2.service.InvoiceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/invoices")
@Tag(name = "Facturas", description = "Operaciones relacionadas con la gestión de facturas")
@SecurityRequirement(name = "bearerAuth")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Operation(
        summary = "Crear una nueva factura",
        description = "Genera una nueva factura en el sistema a partir de los datos enviados.",
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Factura creada exitosamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Invoice.class))
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Solicitud inválida o datos incorrectos",
                content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                responseCode = "401",
                description = "No autorizado o token inválido",
                content = @Content(mediaType = "application/json")
            )
        }
    )
    @PostMapping
    public ResponseEntity<Invoice> createInvoice(
        @Parameter(description = "Datos necesarios para crear una nueva factura", required = true)
        @RequestBody InvoiceRequest request
    ) {
        Invoice newInvoice = invoiceService.createInvoice(request);
        return new ResponseEntity<>(newInvoice, HttpStatus.CREATED);
    }

    @Operation(
        summary = "Obtener una factura por ID",
        description = "Devuelve la información detallada de una factura específica según su identificador.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Factura encontrada correctamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Invoice.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Factura no encontrada",
                content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                responseCode = "401",
                description = "No autorizado o token inválido",
                content = @Content(mediaType = "application/json")
            )
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoice(
        @Parameter(description = "ID de la factura a consultar", example = "1", required = true)
        @PathVariable Long id
    ) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(invoice);
    }

    @Operation(
        summary = "Listar todas las facturas",
        description = "Devuelve una lista de todas las facturas registradas en el sistema.",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de facturas obtenida correctamente",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = Invoice.class))
            ),
            @ApiResponse(
                responseCode = "401",
                description = "No autorizado o token inválido",
                content = @Content(mediaType = "application/json")
            )
        }
    )
    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        return ResponseEntity.ok(invoices);
    }
}
