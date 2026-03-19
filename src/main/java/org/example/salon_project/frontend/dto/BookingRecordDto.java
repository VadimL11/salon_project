package org.example.salon_project.frontend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record BookingRecordDto(
        String id,
        BookingCustomerDto customer,
        String categoryId,
        String serviceId,
        String masterId,
        String date,
        String time,
        String status,
        String note,
        String createdAt) {
}
