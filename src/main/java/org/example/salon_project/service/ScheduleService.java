package org.example.salon_project.service;

import org.example.salon_project.dto.*;

import java.time.LocalDate;

public interface ScheduleService {
    PageDto<ScheduleDto> list(Long masterId, LocalDate from, LocalDate to, Boolean available, int limit, int offset);

    ScheduleDto create(ScheduleCreateRequest request);
}
