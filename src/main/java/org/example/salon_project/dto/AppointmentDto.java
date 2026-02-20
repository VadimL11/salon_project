package org.example.salon_project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AppointmentDto {
    private Long id;
    private Long clientId;
    private Long masterId;
    private Long serviceId;
    private Long scheduleId;
    private String status;
    private BigDecimal price;
    private OffsetDateTime createdAt;
}
