package org.example.salon_project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MasterUpdateRequest {
    private String firstName;
    private String lastName;
    private String photo;
    private Boolean active;
}
