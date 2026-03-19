package org.example.salon_project.frontend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password) {
}
