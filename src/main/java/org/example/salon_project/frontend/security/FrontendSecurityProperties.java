package org.example.salon_project.frontend.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "frontend.security")
@Getter
@Setter
public class FrontendSecurityProperties {

    private List<String> allowedOriginPatterns = new ArrayList<>(List.of(
            "http://localhost:3000",
            "http://127.0.0.1:3000",
            "https://*.up.railway.app",
            "https://*.railway.app"));

    private Duration authCookieMaxAge = Duration.ofDays(7);

    public CorsConfiguration toCorsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(allowedOriginPatterns);
        configuration.setAllowedMethods(List.of(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(Duration.ofHours(1));
        return configuration;
    }

}
