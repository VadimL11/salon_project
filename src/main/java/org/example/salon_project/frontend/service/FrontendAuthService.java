package org.example.salon_project.frontend.service;

import lombok.RequiredArgsConstructor;
import org.example.salon_project.domain.Client;
import org.example.salon_project.frontend.dto.AuthResultDto;
import org.example.salon_project.frontend.dto.FrontendSessionDto;
import org.example.salon_project.frontend.dto.FrontendUserDto;
import org.example.salon_project.frontend.dto.GuestRequest;
import org.example.salon_project.frontend.dto.LoginRequest;
import org.example.salon_project.frontend.dto.RegisterRequest;
import org.example.salon_project.frontend.security.FrontendSecurityConstants;
import org.example.salon_project.frontend.mapper.FrontendMapper;
import org.example.salon_project.repository.ClientRepository;
import org.example.salon_project.security.RoleType;
import org.example.salon_project.security.TokenService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FrontendAuthService {

    private final ClientRepository clientRepository;
    private final FrontendMapper mapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthOutcome register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        if (clientRepository.findByEmailIgnoreCase(normalizedEmail).isPresent()) {
            return new AuthOutcome(new AuthResultDto(false, null, "email_exists"), null);
        }

        Client client = Client.builder()
                .externalId(makeId("account"))
                .firstName(request.firstName().trim())
                .lastName(request.lastName().trim())
                .phone(normalizePhone(request.phone()))
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.password()))
                .role("client")
                .build();

        Client saved = clientRepository.save(client);
        return authenticatedOutcome(saved);
    }

    public AuthOutcome login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        Optional<Client> clientOptional = clientRepository.findByEmailIgnoreCase(normalizedEmail);
        if (clientOptional.isEmpty()) {
            return new AuthOutcome(new AuthResultDto(false, null, "invalid_credentials"), null);
        }

        Client client = clientOptional.get();
        if (client.getPasswordHash() == null || !passwordEncoder.matches(request.password(), client.getPasswordHash())) {
            return new AuthOutcome(new AuthResultDto(false, null, "invalid_credentials"), null);
        }

        return authenticatedOutcome(client);
    }

    public AuthOutcome guest(GuestRequest request) {
        String token = tokenService.generateToken(makeId(FrontendSecurityConstants.GUEST_ID_PREFIX), RoleType.GUEST);
        return new AuthOutcome(new AuthResultDto(true, "guest", null), token);
    }

    public FrontendSessionDto session(String externalId) {
        if (externalId == null) {
            return new FrontendSessionDto(false, null, null);
        }

        return clientRepository.findByExternalId(externalId)
                .map(client -> new FrontendSessionDto(true, client.getRole(), mapper.toUserDto(client)))
                .orElseGet(() -> new FrontendSessionDto(false, null, null));
    }

    public FrontendSessionDto guestSession() {
        return new FrontendSessionDto(true, "guest", null);
    }

    public FrontendUserDto findUser(String externalId) {
        return clientRepository.findByExternalId(externalId)
                .map(mapper::toUserDto)
                .orElse(null);
    }

    public Client findClient(String externalId) {
        return clientRepository.findByExternalId(externalId).orElse(null);
    }

    private AuthOutcome authenticatedOutcome(Client client) {
        String role = "admin".equalsIgnoreCase(client.getRole()) ? "admin" : "client";
        RoleType roleType = "admin".equals(role) ? RoleType.ADMIN : RoleType.CLIENT;
        String token = tokenService.generateToken(client.getExternalId(), roleType);
        return new AuthOutcome(new AuthResultDto(true, role, null), token);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }

    private String makeId(String prefix) {
        return prefix + "-" + UUID.randomUUID();
    }

    private String normalizePhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            return "";
        }
        return phone.trim();
    }

    public record AuthOutcome(AuthResultDto body, String token) {
    }
}
