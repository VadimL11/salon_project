package org.example.salon_project.mapper;

import org.example.salon_project.domain.Category;
import org.example.salon_project.dto.CategoryCreateRequest;
import org.example.salon_project.dto.CategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryCreateRequest request);
}
