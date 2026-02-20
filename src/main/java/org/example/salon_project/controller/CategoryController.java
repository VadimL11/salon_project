package org.example.salon_project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.salon_project.dto.CategoryCreateRequest;
import org.example.salon_project.dto.CategoryDto;
import org.example.salon_project.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Service category management")
public class CategoryController {

    private final CategoryService service;

    @GetMapping
    @Operation(summary = "List all categories")
    public List<CategoryDto> list() {
        return service.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new category")
    public CategoryDto create(@Valid @RequestBody CategoryCreateRequest request) {
        return service.create(request);
    }
}
