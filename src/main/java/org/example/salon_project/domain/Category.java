package org.example.salon_project.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "external_id", unique = true)
    private String externalId;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "icon")
    private String icon;

    @Column(name = "description")
    private String description;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "ua", column = @Column(name = "title_ua", nullable = false)),
            @AttributeOverride(name = "de", column = @Column(name = "title_de", nullable = false)),
            @AttributeOverride(name = "gb", column = @Column(name = "title_gb", nullable = false))
    })
    @Builder.Default
    private LocalizedTextEmbeddable title = LocalizedTextEmbeddable.builder().build();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "ua", column = @Column(name = "description_ua", nullable = false)),
            @AttributeOverride(name = "de", column = @Column(name = "description_de", nullable = false)),
            @AttributeOverride(name = "gb", column = @Column(name = "description_gb", nullable = false))
    })
    @Builder.Default
    private LocalizedTextEmbeddable localizedDescription = LocalizedTextEmbeddable.builder().build();

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;
}
