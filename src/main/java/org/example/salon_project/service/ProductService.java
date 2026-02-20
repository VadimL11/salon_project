package org.example.salon_project.service;

import org.example.salon_project.dto.*;

public interface ProductService {
    PageDto<ProductDto> list(Boolean inStock, int limit, int offset);

    ProductDto create(ProductCreateRequest request);
}
