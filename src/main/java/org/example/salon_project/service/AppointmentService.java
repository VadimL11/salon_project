package org.example.salon_project.service;

import org.example.salon_project.dto.*;

public interface AppointmentService {
    PageDto<AppointmentDto> list(Long clientId, Long masterId, String status, int limit, int offset);

    AppointmentDto getById(Long id);

    AppointmentDto create(AppointmentCreateRequest request);

    AppointmentDto update(Long id, AppointmentUpdateRequest request);
}
