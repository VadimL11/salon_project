package org.example.salon_project.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.salon_project.domain.Client;
import org.example.salon_project.dto.*;
import org.example.salon_project.exception.ResourceNotFoundException;
import org.example.salon_project.mapper.ClientMapper;
import org.example.salon_project.repository.ClientRepository;
import org.example.salon_project.service.ClientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;
    private final ClientMapper mapper;

    @Override
    public PageDto<ClientDto> list(int limit, int offset) {
        Page<Client> page = repository.findAll(PageRequest.of(offset / limit, limit));
        return PageDto.<ClientDto>builder()
                .items(page.map(mapper::toDto).toList())
                .limit(limit)
                .offset(offset)
                .total(page.getTotalElements())
                .build();
    }

    @Override
    public ClientDto getById(Long id) {
        return mapper.toDto(findOrThrow(id));
    }

    @Override
    @Transactional
    public ClientDto create(ClientCreateRequest request) {
        return mapper.toDto(repository.save(mapper.toEntity(request)));
    }

    @Override
    @Transactional
    public ClientDto update(Long id, ClientUpdateRequest request) {
        Client client = findOrThrow(id);
        mapper.updateEntity(request, client);
        return mapper.toDto(repository.save(client));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.delete(findOrThrow(id));
    }

    private Client findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }
}
