package org.example.salon_project.frontend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.example.salon_project.frontend.dto.LoginRequest;
import org.example.salon_project.frontend.dto.RegisterRequest;
import org.example.salon_project.frontend.security.FrontendSecurityConstants;
import org.example.salon_project.repository.BeautyServiceRepository;
import org.example.salon_project.repository.BookingSlotRepository;
import org.example.salon_project.repository.CategoryRepository;
import org.example.salon_project.repository.ClientRepository;
import org.example.salon_project.repository.DrinkRepository;
import org.example.salon_project.repository.MasterRepository;
import org.example.salon_project.repository.ProductRepository;
import org.example.salon_project.repository.SalonBookingRepository;
import org.example.salon_project.repository.TrendRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class FrontendIntegrationTestSupport {

    protected static final String ADMIN_EMAIL = "admin@tintel.beauty";
    protected static final String ADMIN_PASSWORD = "golden-admin";

    @Autowired
    protected MockMvc mockMvc;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    protected ClientRepository clientRepository;

    @Autowired
    protected CategoryRepository categoryRepository;

    @Autowired
    protected BeautyServiceRepository beautyServiceRepository;

    @Autowired
    protected MasterRepository masterRepository;

    @Autowired
    protected BookingSlotRepository bookingSlotRepository;

    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    protected DrinkRepository drinkRepository;

    @Autowired
    protected TrendRepository trendRepository;

    @Autowired
    protected SalonBookingRepository bookingRepository;

    private final Set<String> externalIdsToCleanup = new LinkedHashSet<>();
    private final Set<String> clientEmailsToCleanup = new LinkedHashSet<>();

    @AfterEach
    void cleanupTrackedData() {
        externalIdsToCleanup.forEach(id -> bookingRepository.findByExternalId(id).ifPresent(bookingRepository::delete));
        externalIdsToCleanup.forEach(id -> beautyServiceRepository.findByExternalId(id).ifPresent(beautyServiceRepository::delete));
        externalIdsToCleanup.forEach(id -> masterRepository.findByExternalId(id).ifPresent(masterRepository::delete));
        externalIdsToCleanup.forEach(id -> bookingSlotRepository.findByExternalId(id).ifPresent(bookingSlotRepository::delete));
        externalIdsToCleanup.forEach(id -> productRepository.findByExternalId(id).ifPresent(productRepository::delete));
        externalIdsToCleanup.forEach(id -> drinkRepository.findByExternalId(id).ifPresent(drinkRepository::delete));
        externalIdsToCleanup.forEach(id -> trendRepository.findByExternalId(id).ifPresent(trendRepository::delete));
        externalIdsToCleanup.forEach(id -> categoryRepository.findByExternalId(id).ifPresent(categoryRepository::delete));
        clientEmailsToCleanup.forEach(email -> clientRepository.findByEmailIgnoreCase(email).ifPresent(clientRepository::delete));

        externalIdsToCleanup.clear();
        clientEmailsToCleanup.clear();
    }

    protected Cookie loginAsAdmin() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/frontend/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new LoginRequest(ADMIN_EMAIL, ADMIN_PASSWORD))))
                .andExpect(status().isOk())
                .andReturn();

        return extractAuthCookie(result);
    }

    protected Cookie registerClient(String firstName, String lastName, String phone, String email, String password) throws Exception {
        clientEmailsToCleanup.add(email.trim().toLowerCase(Locale.ROOT));

        MvcResult result = mockMvc.perform(post("/api/v1/frontend/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new RegisterRequest(firstName, lastName, phone, email, password))))
                .andExpect(status().isOk())
                .andReturn();

        return extractAuthCookie(result);
    }

    protected String testExternalId(String prefix) {
        String value = "test-" + prefix + "-" + UUID.randomUUID();
        externalIdsToCleanup.add(value);
        return value;
    }

    protected String testEmail(String prefix) {
        String value = prefix + "+" + UUID.randomUUID() + "@example.com";
        clientEmailsToCleanup.add(value.toLowerCase(Locale.ROOT));
        return value;
    }

    protected JsonNode readBody(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    protected String json(Object body) throws JsonProcessingException {
        return objectMapper.writeValueAsString(body);
    }

    protected Cookie extractAuthCookie(MvcResult result) {
        String header = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);
        assertThat(header).contains(FrontendSecurityConstants.AUTH_COOKIE_NAME + "=");
        String value = extractCookieValue(header, FrontendSecurityConstants.AUTH_COOKIE_NAME);
        return new Cookie(FrontendSecurityConstants.AUTH_COOKIE_NAME, value);
    }

    protected void assertExactFields(JsonNode node, String... expectedFields) {
        Set<String> actual = StreamSupport.stream(((Iterable<String>) node::fieldNames).spliterator(), false)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        assertThat(actual).containsExactlyInAnyOrder(expectedFields);
    }

    protected void assertLocalizedTextShape(JsonNode node) {
        assertExactFields(node, "UA", "DE", "GB");
    }

    private String extractCookieValue(String setCookieHeader, String cookieName) {
        String prefix = cookieName + "=";
        int start = setCookieHeader.indexOf(prefix);
        assertThat(start).isGreaterThanOrEqualTo(0);
        start += prefix.length();

        int end = setCookieHeader.indexOf(';', start);
        if (end < 0) {
            end = setCookieHeader.length();
        }

        return setCookieHeader.substring(start, end);
    }
}
