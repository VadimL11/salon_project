package org.example.salon_project.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.salon_project.domain.Category;
import org.example.salon_project.dto.CategoryCreateRequest;
import org.example.salon_project.dto.CategoryDto;
import org.example.salon_project.mapper.CategoryMapper;
import org.example.salon_project.repository.CategoryRepository;
import org.example.salon_project.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Override
    public List<CategoryDto> list() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    @Transactional
    public CategoryDto create(CategoryCreateRequest request) {
        return mapper.toDto(repository.save(mapper.toEntity(request)));
    }
}
