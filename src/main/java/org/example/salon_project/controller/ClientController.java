package org.example.salon_project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.salon_project.dto.*;
import org.example.salon_project.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Client management")
public class ClientController {

    private final ClientService service;

    @GetMapping
    @Operation(summary = "List clients with pagination")
    public PageDto<ClientDto> list(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return service.list(limit, offset);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get client by ID")
    public ClientDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new client")
    public ClientDto create(@Valid @RequestBody ClientCreateRequest request) {
        return service.create(request);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a client")
    public ClientDto update(@PathVariable Long id, @Valid @RequestBody ClientUpdateRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a client")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
