package org.example.salon_project.frontend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record CareOrderRecordDto(
        String id,
        String email,
        String firstName,
        String lastName,
        String phone,
        List<CartItemDto> items,
        BigDecimal total,
        String createdAt) {
}
