package org.example.salon_project.service;

import org.example.salon_project.dto.*;

public interface MasterService {
    PageDto<MasterDto> list(Boolean active, int limit, int offset);

    MasterDto getById(Long id);

    MasterDto create(MasterCreateRequest request);

    MasterDto update(Long id, MasterUpdateRequest request);

    void delete(Long id);
}
