package org.example.salon_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Getter
@NoArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "name must not be blank")
    private String name;

    private String brand;

    @NotNull(message = "price is required")
    private BigDecimal price;

    private Integer stock;
}
