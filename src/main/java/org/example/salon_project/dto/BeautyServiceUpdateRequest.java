package org.example.salon_project.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class BeautyServiceUpdateRequest {
    private Long categoryId;
    private String name;

    @Min(value = 1, message = "durationMinutes must be at least 1")
    private Integer durationMinutes;

    private BigDecimal price;
    private Boolean active;
}
