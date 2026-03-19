package org.example.salon_project.frontend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record MasterSaveRequest(
        String id,
        @NotBlank String name,
        @NotNull @Valid LocalizedTextDto role,
        @NotBlank String initials,
        @NotBlank String experienceLabel,
        List<String> specialtyCategoryIds,
        List<@Valid MasterCredentialSaveRequest> credentials) {
}
