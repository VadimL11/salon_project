package org.example.salon_project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleDto {
    private Long id;
    private Long masterId;
    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;
}
