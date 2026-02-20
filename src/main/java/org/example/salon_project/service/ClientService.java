package org.example.salon_project.service;

import org.example.salon_project.dto.*;

public interface ClientService {
    PageDto<ClientDto> list(int limit, int offset);

    ClientDto getById(Long id);

    ClientDto create(ClientCreateRequest request);

    ClientDto update(Long id, ClientUpdateRequest request);

    void delete(Long id);
}
