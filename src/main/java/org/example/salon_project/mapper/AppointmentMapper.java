package org.example.salon_project.mapper;

import org.example.salon_project.domain.Appointment;
import org.example.salon_project.dto.AppointmentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "masterId", source = "master.id")
    @Mapping(target = "serviceId", source = "service.id")
    @Mapping(target = "scheduleId", source = "schedule.id")
    AppointmentDto toDto(Appointment appointment);
}
