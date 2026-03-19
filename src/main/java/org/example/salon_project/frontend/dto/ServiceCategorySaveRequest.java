package org.example.salon_project.frontend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record ServiceCategorySaveRequest(
        String id,
        @NotBlank String slug,
        @NotBlank String icon,
        @NotNull @Valid LocalizedTextDto title,
        @NotNull @Valid LocalizedTextDto description) {
}
