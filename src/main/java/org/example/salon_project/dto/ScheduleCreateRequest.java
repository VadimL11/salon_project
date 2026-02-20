package org.example.salon_project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleCreateRequest {

    @NotNull(message = "masterId is required")
    private Long masterId;

    @NotNull(message = "workDate is required")
    private LocalDate workDate;

    @NotNull(message = "startTime is required")
    private LocalTime startTime;

    @NotNull(message = "endTime is required")
    private LocalTime endTime;

    private Boolean available;
}
