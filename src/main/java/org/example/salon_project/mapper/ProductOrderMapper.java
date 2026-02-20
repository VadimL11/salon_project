package org.example.salon_project.mapper;

import org.example.salon_project.domain.ProductOrder;
import org.example.salon_project.dto.ProductOrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductOrderMapper {

    @Mapping(target = "appointmentId", source = "appointment.id")
    @Mapping(target = "productId", source = "product.id")
    ProductOrderDto toDto(ProductOrder order);
}
