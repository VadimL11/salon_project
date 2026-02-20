package org.example.salon_project.mapper;

import org.example.salon_project.domain.Master;
import org.example.salon_project.dto.MasterCreateRequest;
import org.example.salon_project.dto.MasterDto;
import org.example.salon_project.dto.MasterUpdateRequest;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface MasterMapper {

    MasterDto toDto(Master master);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    @Mapping(target = "active", defaultValue = "true")
    Master toEntity(MasterCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rating", ignore = true)
    void updateEntity(MasterUpdateRequest request, @MappingTarget Master master);
}
