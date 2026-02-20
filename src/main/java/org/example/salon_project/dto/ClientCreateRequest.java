package org.example.salon_project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClientCreateRequest {

    @NotBlank(message = "firstName must not be blank")
    private String firstName;

    @NotBlank(message = "lastName must not be blank")
    private String lastName;

    private String phone;

    @Email(message = "email must be a valid email")
    private String email;

    private String language;
}
