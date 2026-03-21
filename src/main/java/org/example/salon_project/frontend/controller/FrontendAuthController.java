package org.example.salon_project.frontend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.salon_project.frontend.dto.AuthResultDto;
import org.example.salon_project.frontend.dto.FrontendSessionDto;
import org.example.salon_project.frontend.dto.GuestRequest;
import org.example.salon_project.frontend.dto.LoginRequest;
import org.example.salon_project.frontend.dto.RegisterRequest;
import org.example.salon_project.frontend.security.FrontendSecurityConstants;
import org.example.salon_project.frontend.security.FrontendSecurityProperties;
import org.example.salon_project.frontend.service.FrontendAuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/frontend/auth")
@RequiredArgsConstructor
public class FrontendAuthController {

    private static final String CROSS_SITE_SAME_SITE = "None";
    private static final String LOCAL_SAME_SITE = "Lax";

    private final FrontendAuthService authService;
    private final FrontendSecurityProperties frontendSecurityProperties;

    @PostMapping("/register")
    public AuthResultDto register(@Valid @RequestBody RegisterRequest request,
                                  HttpServletRequest requestContext,
                                  HttpServletResponse response) {
        var outcome = authService.register(request);
        setCookieIfNeeded(requestContext, response, outcome.token());
        return outcome.body();
    }

    @PostMapping("/login")
    public AuthResultDto login(@Valid @RequestBody LoginRequest request,
                               HttpServletRequest requestContext,
                               HttpServletResponse response) {
        var outcome = authService.login(request);
        setCookieIfNeeded(requestContext, response, outcome.token());
        return outcome.body();
    }

    @PostMapping("/guest")
    public AuthResultDto guest(@RequestBody(required = false) GuestRequest request,
                               HttpServletRequest requestContext,
                               HttpServletResponse response) {
        var outcome = authService.guest(request);
        setCookieIfNeeded(requestContext, response, outcome.token());
        return outcome.body();
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildAuthCookie(request, "", Duration.ZERO).toString());
    }

    @GetMapping("/session")
    public FrontendSessionDto session(Authentication authentication) {
        if (hasRole(authentication, "ROLE_GUEST")) {
            return authService.guestSession();
        }
        return authService.session(authentication != null ? authentication.getName() : null);
    }

    private void setCookieIfNeeded(HttpServletRequest request, HttpServletResponse response, String token) {
        if (token == null || token.isBlank()) {
            return;
        }

        response.addHeader(HttpHeaders.SET_COOKIE, buildAuthCookie(request, token, frontendSecurityProperties.getAuthCookieMaxAge()).toString());
    }

    private ResponseCookie buildAuthCookie(HttpServletRequest request, String token, Duration maxAge) {
        boolean secureRequest = request.isSecure();
        return ResponseCookie.from(FrontendSecurityConstants.AUTH_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(secureRequest)
                .path("/")
                .sameSite(secureRequest ? CROSS_SITE_SAME_SITE : LOCAL_SAME_SITE)
                .maxAge(maxAge)
                .build();
    }

    private boolean hasRole(Authentication authentication, String role) {
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }
}
