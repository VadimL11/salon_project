package org.example.salon_project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

// -------- Response --------
@Getter
@Setter
@NoArgsConstructor
public class MasterDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String photo;
    private BigDecimal rating;
    private boolean active;
}
