package org.example.salon_project.mapper;

import org.example.salon_project.domain.DrinkOrder;
import org.example.salon_project.dto.DrinkOrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DrinkOrderMapper {

    @Mapping(target = "appointmentId", source = "appointment.id")
    @Mapping(target = "drinkId", source = "drink.id")
    DrinkOrderDto toDto(DrinkOrder order);
}
