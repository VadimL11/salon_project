package org.example.salon_project.frontend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record MasterCredentialSaveRequest(
        String id,
        @NotBlank String name,
        @NotBlank String type,
        @NotBlank String fileUrl) {
}
