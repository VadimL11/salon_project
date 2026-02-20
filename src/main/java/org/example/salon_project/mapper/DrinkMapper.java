package org.example.salon_project.mapper;

import org.example.salon_project.domain.Drink;
import org.example.salon_project.dto.DrinkCreateRequest;
import org.example.salon_project.dto.DrinkDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DrinkMapper {

    DrinkDto toDto(Drink drink);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "available", defaultValue = "true")
    Drink toEntity(DrinkCreateRequest request);
}
