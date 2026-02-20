package org.example.salon_project.service;

import org.example.salon_project.dto.*;

public interface BeautyServiceService {
    PageDto<BeautyServiceDto> list(Long categoryId, Boolean active, int limit, int offset);

    BeautyServiceDto create(BeautyServiceCreateRequest request);

    BeautyServiceDto update(Long id, BeautyServiceUpdateRequest request);

    void delete(Long id);
}
