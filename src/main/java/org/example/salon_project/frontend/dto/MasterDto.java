package org.example.salon_project.frontend.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public record MasterDto(
        String id,
        String name,
        LocalizedTextDto role,
        String initials,
        String experienceLabel,
        List<String> specialtyCategoryIds,
        List<MasterCredentialDto> credentials) {
}
