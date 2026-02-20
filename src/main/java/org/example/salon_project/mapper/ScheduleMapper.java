package org.example.salon_project.mapper;

import org.example.salon_project.domain.Schedule;
import org.example.salon_project.dto.ScheduleDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {

    @Mapping(target = "masterId", source = "master.id")
    ScheduleDto toDto(Schedule schedule);
}
