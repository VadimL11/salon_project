package org.example.salon_project.frontend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record CareProductSaveRequest(
        String id,
        @NotNull @Valid LocalizedTextDto title,
        @NotBlank String brand,
        @NotNull @DecimalMin("0.0") BigDecimal price,
        @NotBlank String icon) {
}
