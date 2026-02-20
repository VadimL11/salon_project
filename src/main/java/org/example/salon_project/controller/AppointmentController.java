package org.example.salon_project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.salon_project.dto.*;
import org.example.salon_project.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Appointment booking and management")
public class AppointmentController {

    private final AppointmentService service;

    @GetMapping
    @Operation(summary = "List appointments with optional filters")
    public PageDto<AppointmentDto> list(
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) Long masterId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return service.list(clientId, masterId, status, limit, offset);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID")
    public AppointmentDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Book a new appointment")
    public AppointmentDto create(@Valid @RequestBody AppointmentCreateRequest request) {
        return service.create(request);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update appointment status or price")
    public AppointmentDto update(@PathVariable Long id,
            @Valid @RequestBody AppointmentUpdateRequest request) {
        return service.update(id, request);
    }
}
