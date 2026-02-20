package org.example.salon_project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class DrinkDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private boolean available;
}
