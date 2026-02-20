package org.example.salon_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class DrinkCreateRequest {

    @NotBlank(message = "name must not be blank")
    private String name;

    @NotNull(message = "price is required")
    private BigDecimal price;

    private Boolean available;
}
