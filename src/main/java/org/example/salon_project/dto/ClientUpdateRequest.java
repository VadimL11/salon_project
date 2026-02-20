package org.example.salon_project.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClientUpdateRequest {
    private String firstName;
    private String lastName;
    private String phone;

    @Email(message = "email must be a valid email")
    private String email;

    private String language;
}
