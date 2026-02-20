package org.example.salon_project.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.salon_project.domain.*;
import org.example.salon_project.dto.*;
import org.example.salon_project.exception.ConflictException;
import org.example.salon_project.exception.ResourceNotFoundException;
import org.example.salon_project.mapper.AppointmentMapper;
import org.example.salon_project.repository.*;
import org.example.salon_project.service.AppointmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final ClientRepository clientRepo;
    private final MasterRepository masterRepo;
    private final BeautyServiceRepository serviceRepo;
    private final ScheduleRepository scheduleRepo;
    private final AppointmentMapper mapper;

    @Override
    public PageDto<AppointmentDto> list(Long clientId, Long masterId, String status, int limit, int offset) {
        Page<Appointment> page = appointmentRepo.findWithFilters(clientId, masterId, status,
                PageRequest.of(offset / limit, limit));
        return PageDto.<AppointmentDto>builder()
                .items(page.map(mapper::toDto).toList())
                .limit(limit)
                .offset(offset)
                .total(page.getTotalElements())
                .build();
    }

    @Override
    public AppointmentDto getById(Long id) {
        return mapper.toDto(findOrThrow(id));
    }

    @Override
    @Transactional
    public AppointmentDto create(AppointmentCreateRequest request) {
        // Prevent double-booking
        if (appointmentRepo.existsByScheduleId(request.getScheduleId())) {
            throw new ConflictException("Schedule slot " + request.getScheduleId() + " is already booked");
        }

        Client client = clientRepo.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", request.getClientId()));
        Master master = masterRepo.findById(request.getMasterId())
                .orElseThrow(() -> new ResourceNotFoundException("Master", request.getMasterId()));
        BeautyService service = serviceRepo.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("BeautyService", request.getServiceId()));
        Schedule schedule = scheduleRepo.findById(request.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", request.getScheduleId()));

        Appointment appointment = new Appointment();
        appointment.setClient(client);
        appointment.setMaster(master);
        appointment.setService(service);
        appointment.setSchedule(schedule);
        appointment.setStatus("PENDING");
        appointment.setPrice(service.getPrice());
        return mapper.toDto(appointmentRepo.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentDto update(Long id, AppointmentUpdateRequest request) {
        Appointment appointment = findOrThrow(id);
        if (request.getStatus() != null)
            appointment.setStatus(request.getStatus());
        if (request.getPrice() != null)
            appointment.setPrice(request.getPrice());
        return mapper.toDto(appointmentRepo.save(appointment));
    }

    private Appointment findOrThrow(Long id) {
        return appointmentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
    }
}
