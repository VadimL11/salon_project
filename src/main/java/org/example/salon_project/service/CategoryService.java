package org.example.salon_project.service;

import org.example.salon_project.dto.CategoryCreateRequest;
import org.example.salon_project.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> list();

    CategoryDto create(CategoryCreateRequest request);
}
