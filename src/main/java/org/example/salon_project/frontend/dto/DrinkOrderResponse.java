package org.example.salon_project.frontend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record DrinkOrderResponse(
        boolean ordered,
        String id,
        String email,
        String drinkId,
        String createdAt) {
}
