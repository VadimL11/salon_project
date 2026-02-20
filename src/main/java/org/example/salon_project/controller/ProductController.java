package org.example.salon_project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.salon_project.dto.*;
import org.example.salon_project.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Salon retail product catalogue")
public class ProductController {

    private final ProductService service;

    @GetMapping
    @Operation(summary = "List products, optionally filter to in-stock only")
    public PageDto<ProductDto> list(
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return service.list(inStock, limit, offset);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new product")
    public ProductDto create(@Valid @RequestBody ProductCreateRequest request) {
        return service.create(request);
    }
}
