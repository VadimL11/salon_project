package org.example.salon_project.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.salon_project.domain.Drink;
import org.example.salon_project.dto.DrinkCreateRequest;
import org.example.salon_project.dto.DrinkDto;
import org.example.salon_project.mapper.DrinkMapper;
import org.example.salon_project.repository.DrinkRepository;
import org.example.salon_project.service.DrinkService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrinkServiceImpl implements DrinkService {

    private final DrinkRepository repository;
    private final DrinkMapper mapper;

    @Override
    public List<DrinkDto> list(Boolean available) {
        List<Drink> drinks = (available != null)
                ? repository.findByAvailable(available)
                : repository.findAll();
        return drinks.stream().map(mapper::toDto).toList();
    }

    @Override
    @Transactional
    public DrinkDto create(DrinkCreateRequest request) {
        return mapper.toDto(repository.save(mapper.toEntity(request)));
    }
}
