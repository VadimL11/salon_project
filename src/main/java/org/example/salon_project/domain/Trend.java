package org.example.salon_project.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trends")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, unique = true)
    private String externalId;

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
    private LocalizedTextEmbeddable description = LocalizedTextEmbeddable.builder().build();

    @Column(name = "gradient", nullable = false)
    private String gradient;

    @Column(name = "emoji", nullable = false)
    private String emoji;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;
}
