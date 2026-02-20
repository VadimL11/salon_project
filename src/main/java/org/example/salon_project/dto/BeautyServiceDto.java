package org.example.salon_project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class BeautyServiceDto {
    private Long id;
    private Long categoryId;
    private String name;
    private Integer durationMinutes;
    private BigDecimal price;
    private boolean active;
}
