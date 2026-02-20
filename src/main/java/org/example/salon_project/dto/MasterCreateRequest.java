package org.example.salon_project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MasterCreateRequest {

    @NotBlank(message = "firstName must not be blank")
    private String firstName;

    @NotBlank(message = "lastName must not be blank")
    private String lastName;

    private String photo;
    private Boolean active;
}
