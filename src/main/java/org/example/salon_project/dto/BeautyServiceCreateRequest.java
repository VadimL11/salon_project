package org.example.salon_project.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class BeautyServiceCreateRequest {

    @NotNull(message = "categoryId is required")
    private Long categoryId;

    @NotBlank(message = "name must not be blank")
    private String name;

    @NotNull(message = "durationMinutes is required")
    @Min(value = 1, message = "durationMinutes must be at least 1")
    private Integer durationMinutes;

    @NotNull(message = "price is required")
    private BigDecimal price;

    private Boolean active;
}
