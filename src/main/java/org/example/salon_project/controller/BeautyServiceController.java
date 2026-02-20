package org.example.salon_project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.salon_project.dto.*;
import org.example.salon_project.service.BeautyServiceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
@Tag(name = "Beauty Services", description = "Salon service catalogue management")
public class BeautyServiceController {

    private final BeautyServiceService service;

    @GetMapping
    @Operation(summary = "List beauty services with optional filters")
    public PageDto<BeautyServiceDto> list(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return service.list(categoryId, active, limit, offset);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new beauty service")
    public BeautyServiceDto create(@Valid @RequestBody BeautyServiceCreateRequest request) {
        return service.create(request);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a beauty service")
    public BeautyServiceDto update(@PathVariable Long id,
            @Valid @RequestBody BeautyServiceUpdateRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a beauty service")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
