package org.example.salon_project.mapper;

import org.example.salon_project.domain.Review;
import org.example.salon_project.dto.ReviewDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "masterId", source = "master.id")
    @Mapping(target = "rating", source = "rating")
    ReviewDto toDto(Review review);
}
