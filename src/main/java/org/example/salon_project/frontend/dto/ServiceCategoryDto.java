package org.example.salon_project.frontend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record ServiceCategoryDto(
        String id,
        String slug,
        String icon,
        LocalizedTextDto title,
        LocalizedTextDto description) {
}
