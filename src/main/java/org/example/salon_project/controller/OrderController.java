package org.example.salon_project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.salon_project.dto.*;
import org.example.salon_project.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/appointments/{appointmentId}/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Product and drink orders linked to appointments")
public class OrderController {

    private final OrderService service;

    @PostMapping("/products")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a product order to an appointment")
    public ProductOrderDto addProduct(
            @PathVariable Long appointmentId,
            @Valid @RequestBody ProductOrderCreateRequest request) {
        return service.addProductOrder(appointmentId, request);
    }

    @PostMapping("/drinks")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a drink order to an appointment")
    public DrinkOrderDto addDrink(
            @PathVariable Long appointmentId,
            @Valid @RequestBody DrinkOrderCreateRequest request) {
        return service.addDrinkOrder(appointmentId, request);
    }
}
