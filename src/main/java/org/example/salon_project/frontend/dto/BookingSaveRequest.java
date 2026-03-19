package org.example.salon_project.frontend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record BookingSaveRequest(
        String id,
        @NotNull @Valid BookingCustomerDto customer,
        String categoryId,
        String serviceId,
        String masterId,
        @NotBlank String date,
        @NotBlank String time,
        String status,
        String note,
        String createdAt) {
}
