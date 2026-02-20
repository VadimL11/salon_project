package org.example.salon_project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.salon_project.dto.DrinkCreateRequest;
import org.example.salon_project.dto.DrinkDto;
import org.example.salon_project.service.DrinkService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drinks")
@RequiredArgsConstructor
@Tag(name = "Drinks", description = "Complimentary drink catalogue")
public class DrinkController {

    private final DrinkService service;

    @GetMapping
    @Operation(summary = "List all drinks, optionally filter by availability")
    public List<DrinkDto> list(
            @RequestParam(required = false) Boolean available) {
        return service.list(available);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new drink to the catalogue")
    public DrinkDto create(@Valid @RequestBody DrinkCreateRequest request) {
        return service.create(request);
    }
}
