package org.example.salon_project.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.salon_project.domain.Master;
import org.example.salon_project.domain.Schedule;
import org.example.salon_project.dto.*;
import org.example.salon_project.exception.ResourceNotFoundException;
import org.example.salon_project.mapper.ScheduleMapper;
import org.example.salon_project.repository.MasterRepository;
import org.example.salon_project.repository.ScheduleRepository;
import org.example.salon_project.service.ScheduleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepo;
    private final MasterRepository masterRepo;
    private final ScheduleMapper mapper;

    @Override
    public PageDto<ScheduleDto> list(Long masterId, LocalDate from, LocalDate to, Boolean available, int limit,
            int offset) {
        Page<Schedule> page = scheduleRepo.findWithFilters(masterId, from, to, available,
                PageRequest.of(offset / limit, limit));
        return PageDto.<ScheduleDto>builder()
                .items(page.map(mapper::toDto).toList())
                .limit(limit)
                .offset(offset)
                .total(page.getTotalElements())
                .build();
    }

    @Override
    @Transactional
    public ScheduleDto create(ScheduleCreateRequest request) {
        Master master = masterRepo.findById(request.getMasterId())
                .orElseThrow(() -> new ResourceNotFoundException("Master", request.getMasterId()));
        Schedule schedule = new Schedule();
        schedule.setMaster(master);
        schedule.setWorkDate(request.getWorkDate());
        schedule.setStartTime(request.getStartTime());
        schedule.setEndTime(request.getEndTime());
        schedule.setAvailable(request.getAvailable() == null || request.getAvailable());
        return mapper.toDto(scheduleRepo.save(schedule));
    }
}
