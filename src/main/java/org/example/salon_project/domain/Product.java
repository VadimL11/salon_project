package org.example.salon_project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "brand")
    private String brand;

    @Column(name = "price", nullable = false)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "stock", nullable = false)
    @Builder.Default
    private Integer stock = 0;
}
