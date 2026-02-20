package org.example.salon_project.service;

import org.example.salon_project.dto.DrinkCreateRequest;
import org.example.salon_project.dto.DrinkDto;

import java.util.List;

public interface DrinkService {
    List<DrinkDto> list(Boolean available);

    DrinkDto create(DrinkCreateRequest request);
}
