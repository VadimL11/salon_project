package org.example.salon_project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AppointmentCreateRequest {

    @NotNull(message = "clientId is required")
    private Long clientId;

    @NotNull(message = "masterId is required")
    private Long masterId;

    @NotNull(message = "serviceId is required")
    private Long serviceId;

    @NotNull(message = "scheduleId is required")
    private Long scheduleId;
}
