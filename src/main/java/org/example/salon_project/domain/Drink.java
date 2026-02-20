package org.example.salon_project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "drinks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Drink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "available", nullable = false)
    @Builder.Default
    private boolean available = true;
}
