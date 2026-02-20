package org.example.salon_project.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private final String accessToken;
    private final String tokenType;
    private final int expiresIn;
}
