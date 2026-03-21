package org.example.salon_project.frontend.security;

import lombok.Getter;
import lombok.Setter;
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

    private List<String> allowedOriginPatterns = new ArrayList<>(List.of("*"));

    private Duration authCookieMaxAge = Duration.ofDays(7);
}
