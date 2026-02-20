package org.example.salon_project.mapper;

import org.example.salon_project.domain.Client;
import org.example.salon_project.dto.ClientCreateRequest;
import org.example.salon_project.dto.ClientDto;
import org.example.salon_project.dto.ClientUpdateRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientDto toDto(Client client);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Client toEntity(ClientCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(ClientUpdateRequest request, @MappingTarget Client client);
}
