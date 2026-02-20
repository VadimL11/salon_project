package org.example.salon_project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ProductOrderDto {
    private Long id;
    private Long appointmentId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private OffsetDateTime createdAt;
}
