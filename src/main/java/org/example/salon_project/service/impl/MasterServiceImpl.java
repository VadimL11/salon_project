package org.example.salon_project.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.salon_project.domain.Master;
import org.example.salon_project.dto.*;
import org.example.salon_project.exception.ResourceNotFoundException;
import org.example.salon_project.mapper.MasterMapper;
import org.example.salon_project.repository.MasterRepository;
import org.example.salon_project.service.MasterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MasterServiceImpl implements MasterService {

    private final MasterRepository repository;
    private final MasterMapper mapper;

    @Override
    public PageDto<MasterDto> list(Boolean active, int limit, int offset) {
        PageRequest pr = PageRequest.of(offset / limit, limit);
        Page<Master> page = (active != null)
                ? repository.findByActive(active, pr)
                : repository.findAll(pr);
        return PageDto.<MasterDto>builder()
                .items(page.map(mapper::toDto).toList())
                .limit(limit)
                .offset(offset)
                .total(page.getTotalElements())
                .build();
    }

    @Override
    public MasterDto getById(Long id) {
        return mapper.toDto(findOrThrow(id));
    }

    @Override
    @Transactional
    public MasterDto create(MasterCreateRequest request) {
        Master master = mapper.toEntity(request);
        if (request.getActive() != null)
            master.setActive(request.getActive());
        return mapper.toDto(repository.save(master));
    }

    @Override
    @Transactional
    public MasterDto update(Long id, MasterUpdateRequest request) {
        Master master = findOrThrow(id);
        mapper.updateEntity(request, master);
        return mapper.toDto(repository.save(master));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.delete(findOrThrow(id));
    }

    private Master findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Master", id));
    }
}
