package org.example.salon_project.frontend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record ServiceItemDto(
        String id,
        String categoryId,
        LocalizedTextDto title,
        Integer durationMinutes,
        BigDecimal price) {
}
