package org.example.salon_project.mapper;

import org.example.salon_project.domain.BeautyService;
import org.example.salon_project.dto.BeautyServiceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BeautyServiceMapper {

    @Mapping(target = "categoryId", source = "category.id")
    BeautyServiceDto toDto(BeautyService service);
}
