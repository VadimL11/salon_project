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

    @Column(name = "external_id", unique = true)
    private String externalId;

    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "ua", column = @Column(name = "title_ua", nullable = false)),
            @AttributeOverride(name = "de", column = @Column(name = "title_de", nullable = false)),
            @AttributeOverride(name = "gb", column = @Column(name = "title_gb", nullable = false))
    })
    @Builder.Default
    private LocalizedTextEmbeddable title = LocalizedTextEmbeddable.builder().build();

    @Column(name = "brand")
    private String brand;

    @Column(name = "icon", nullable = false)
    @Builder.Default
    private String icon = "";

    @Column(name = "price", nullable = false)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "stock", nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;
}
