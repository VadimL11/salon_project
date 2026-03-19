package org.example.salon_project.frontend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record ServiceItemSaveRequest(
        String id,
        @NotBlank String categoryId,
        @NotNull @Valid LocalizedTextDto title,
        @NotNull Integer durationMinutes,
        @NotNull @DecimalMin("0.0") BigDecimal price) {
}
