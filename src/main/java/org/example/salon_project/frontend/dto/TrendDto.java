package org.example.salon_project.frontend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record TrendDto(
        String id,
        LocalizedTextDto title,
        LocalizedTextDto description,
        String gradient,
        String emoji,
        String image) {
}
