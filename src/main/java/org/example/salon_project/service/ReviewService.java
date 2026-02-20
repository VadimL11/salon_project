package org.example.salon_project.service;

import org.example.salon_project.dto.ReviewCreateRequest;
import org.example.salon_project.dto.ReviewDto;

public interface ReviewService {
    ReviewDto create(ReviewCreateRequest request);
}
