package org.example.salon_project.frontend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record TrendSaveRequest(
        String id,
        @NotNull @Valid LocalizedTextDto title,
        @NotNull @Valid LocalizedTextDto description,
        @NotBlank String gradient,
        @NotBlank String emoji,
        String image) {
}
