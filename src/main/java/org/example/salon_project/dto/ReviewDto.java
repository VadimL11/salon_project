package org.example.salon_project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReviewDto {
    private Long id;
    private Long clientId;
    private Long masterId;
    private Integer rating;
    private String comment;
    private OffsetDateTime createdAt;
}
