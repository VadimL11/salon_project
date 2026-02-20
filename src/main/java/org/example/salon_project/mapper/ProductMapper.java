package org.example.salon_project.mapper;

import org.example.salon_project.domain.Product;
import org.example.salon_project.dto.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto toDto(Product product);

    @Mapping(target = "id", ignore = true)
    Product toEntity(org.example.salon_project.dto.ProductCreateRequest request);
}
