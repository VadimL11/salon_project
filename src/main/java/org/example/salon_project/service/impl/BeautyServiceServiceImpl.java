package org.example.salon_project.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.salon_project.domain.BeautyService;
import org.example.salon_project.domain.Category;
import org.example.salon_project.dto.*;
import org.example.salon_project.exception.ResourceNotFoundException;
import org.example.salon_project.mapper.BeautyServiceMapper;
import org.example.salon_project.repository.BeautyServiceRepository;
import org.example.salon_project.repository.CategoryRepository;
import org.example.salon_project.service.BeautyServiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BeautyServiceServiceImpl implements BeautyServiceService {

    private final BeautyServiceRepository serviceRepo;
    private final CategoryRepository categoryRepo;
    private final BeautyServiceMapper mapper;

    @Override
    public PageDto<BeautyServiceDto> list(Long categoryId, Boolean active, int limit, int offset) {
        PageRequest pr = PageRequest.of(offset / limit, limit);
        Page<BeautyService> page;
        if (categoryId != null && active != null) {
            page = serviceRepo.findByCategoryIdAndActive(categoryId, active, pr);
        } else if (categoryId != null) {
            page = serviceRepo.findByCategoryId(categoryId, pr);
        } else if (active != null) {
            page = serviceRepo.findByActive(active, pr);
        } else {
            page = serviceRepo.findAll(pr);
        }
        return PageDto.<BeautyServiceDto>builder()
                .items(page.map(mapper::toDto).toList())
                .limit(limit)
                .offset(offset)
                .total(page.getTotalElements())
                .build();
    }

    @Override
    @Transactional
    public BeautyServiceDto create(BeautyServiceCreateRequest request) {
        Category category = categoryRepo.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId()));
        BeautyService service = new BeautyService();
        service.setCategory(category);
        service.setName(request.getName());
        service.setDurationMinutes(request.getDurationMinutes());
        service.setPrice(request.getPrice());
        service.setActive(request.getActive() == null || request.getActive());
        return mapper.toDto(serviceRepo.save(service));
    }

    @Override
    @Transactional
    public BeautyServiceDto update(Long id, BeautyServiceUpdateRequest request) {
        BeautyService service = serviceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BeautyService", id));
        if (request.getCategoryId() != null) {
            service.setCategory(categoryRepo.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", request.getCategoryId())));
        }
        if (request.getName() != null)
            service.setName(request.getName());
        if (request.getDurationMinutes() != null)
            service.setDurationMinutes(request.getDurationMinutes());
        if (request.getPrice() != null)
            service.setPrice(request.getPrice());
        if (request.getActive() != null)
            service.setActive(request.getActive());
        return mapper.toDto(serviceRepo.save(service));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        BeautyService service = serviceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BeautyService", id));
        serviceRepo.delete(service);
    }
}
