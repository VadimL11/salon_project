package org.example.salon_project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.salon_project.dto.ReviewCreateRequest;
import org.example.salon_project.dto.ReviewDto;
import org.example.salon_project.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Client reviews and ratings for masters")
public class ReviewController {

    private final ReviewService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Submit a review for a master")
    public ReviewDto create(@Valid @RequestBody ReviewCreateRequest request) {
        return service.create(request);
    }
}
