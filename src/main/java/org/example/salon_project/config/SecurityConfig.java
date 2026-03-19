package org.example.salon_project.config;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.example.salon_project.security.TokenAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenAuthFilter tokenAuthFilter;

    /**
     * Custom UserDetailsService bean to disable Spring Boot's default in-memory user.
     * We authenticate only via JWT tokens (Bearer).
     */
    @Bean
    UserDetailsService emptyDetailsService() {
        return username -> {
            throw new UsernameNotFoundException("No local users, only JWT tokens allowed!");
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityContext(securityContext ->
                        securityContext
                                .requireExplicitSave(true)
                                .securityContextRepository(
                                        new DelegatingSecurityContextRepository(
                                                new RequestAttributeSecurityContextRepository(),
                                                new HttpSessionSecurityContextRepository()
                                        )
                                )
                )
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(
                                "/ping",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api/v1/auth/**" // наприклад: /login, /sign-up, /refresh
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/frontend/bootstrap",
                                "/api/v1/frontend/service-categories",
                                "/api/v1/frontend/services",
                                "/api/v1/frontend/masters",
                                "/api/v1/frontend/booking-slots",
                                "/api/v1/frontend/care-products",
                                "/api/v1/frontend/drinks",
                                "/api/v1/frontend/trends",
                                "/api/v1/frontend/auth/session").permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/frontend/auth/register",
                                "/api/v1/frontend/auth/login",
                                "/api/v1/frontend/auth/guest",
                                "/api/v1/frontend/auth/logout",
                                "/api/v1/frontend/bookings",
                                "/api/v1/frontend/care-product-checkouts",
                                "/api/v1/frontend/drink-orders").permitAll()
                        .anyRequest().authenticated()
                )
                .cors(cors -> cors.configurationSource(_ -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOriginPatterns(List.of("*"));
                    configuration.setAllowedMethods(List.of("*"));
                    configuration.setAllowedHeaders(List.of("*"));
                    configuration.setAllowCredentials(true);
                    return configuration;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(tokenAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e ->
                        e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
