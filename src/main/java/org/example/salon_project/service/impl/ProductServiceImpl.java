package org.example.salon_project.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.salon_project.domain.Product;
import org.example.salon_project.dto.*;
import org.example.salon_project.mapper.ProductMapper;
import org.example.salon_project.repository.ProductRepository;
import org.example.salon_project.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public PageDto<ProductDto> list(Boolean inStock, int limit, int offset) {
        PageRequest pr = PageRequest.of(offset / limit, limit);
        Page<Product> page = Boolean.TRUE.equals(inStock)
                ? repository.findByStockGreaterThan(0, pr)
                : repository.findAll(pr);
        return PageDto.<ProductDto>builder()
                .items(page.map(mapper::toDto).toList())
                .limit(limit)
                .offset(offset)
                .total(page.getTotalElements())
                .build();
    }

    @Override
    @Transactional
    public ProductDto create(ProductCreateRequest request) {
        return mapper.toDto(repository.save(mapper.toEntity(request)));
    }
}
