package org.example.salon_project.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "masters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Master {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", unique = true)
    private String externalId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "photo")
    private String photo;

    @Column(name = "display_name")
    private String displayName;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "ua", column = @Column(name = "role_ua", nullable = false)),
            @AttributeOverride(name = "de", column = @Column(name = "role_de", nullable = false)),
            @AttributeOverride(name = "gb", column = @Column(name = "role_gb", nullable = false))
    })
    @Builder.Default
    private LocalizedTextEmbeddable role = LocalizedTextEmbeddable.builder().build();

    @Column(name = "initials", nullable = false)
    @Builder.Default
    private String initials = "";

    @Column(name = "experience_label", nullable = false)
    @Builder.Default
    private String experienceLabel = "";

    @Column(name = "rating", nullable = false)
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;

    @ManyToMany
    @JoinTable(name = "master_specialty_categories",
            joinColumns = @JoinColumn(name = "master_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    @Builder.Default
    private Set<Category> specialtyCategories = new LinkedHashSet<>();

    @OneToMany(mappedBy = "master", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<MasterCredential> credentials = new ArrayList<>();

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;
}
