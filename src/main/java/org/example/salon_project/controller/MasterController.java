package org.example.salon_project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.salon_project.dto.*;
import org.example.salon_project.service.MasterService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/masters")
@RequiredArgsConstructor
@Tag(name = "Masters", description = "Master (beautician) management")
public class MasterController {

    private final MasterService service;

    @GetMapping
    @Operation(summary = "List masters, optionally filter by active status")
    public PageDto<MasterDto> list(
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return service.list(active, limit, offset);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get master by ID")
    public MasterDto getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new master")
    public MasterDto create(@Valid @RequestBody MasterCreateRequest request) {
        return service.create(request);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a master")
    public MasterDto update(@PathVariable Long id, @Valid @RequestBody MasterUpdateRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a master")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
