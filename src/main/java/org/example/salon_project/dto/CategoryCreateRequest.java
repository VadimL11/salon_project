package org.example.salon_project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryCreateRequest {

    @NotBlank(message = "name must not be blank")
    private String name;

    private String icon;
    private String description;
}
