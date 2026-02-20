package org.example.salon_project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    @NotBlank
    @Email(message = "email must be a valid email")
    private String email;

    @NotBlank
    @Size(min = 6, message = "password must be at least 6 characters")
    private String password;
}
