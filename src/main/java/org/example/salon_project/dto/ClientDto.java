package org.example.salon_project.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

// -------- Response --------
@Getter
@Setter
@NoArgsConstructor
public class ClientDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String language;
    private OffsetDateTime createdAt;
}
