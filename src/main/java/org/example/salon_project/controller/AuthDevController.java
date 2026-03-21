package org.example.salon_project.controller;

import org.example.salon_project.security.RoleType;
import org.example.salon_project.security.TokenService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@ConditionalOnProperty(prefix = "auth.dev-token", name = "enabled", havingValue = "true")
public class AuthDevController {

    private final TokenService tokenService;

    public AuthDevController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/dev-token")
    public String devToken(
            @RequestParam(defaultValue = "1") String id,
            @RequestParam(defaultValue = "ADMIN") RoleType type
    ) {
        return tokenService.generateToken(id, type);
    }
}
