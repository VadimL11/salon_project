package org.example.salon_project.frontend;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.salon_project.frontend.dto.GuestRequest;
import org.example.salon_project.frontend.dto.LoginRequest;
import org.example.salon_project.frontend.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FrontendAuthApiIntegrationTest extends FrontendIntegrationTestSupport {

    private static final String FRONTEND_ORIGIN = "https://tintel.up.railway.app";

    @Test
    void sessionWithoutCookieIsUnauthenticatedAndMatchesFrontendShape() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/frontend/auth/session"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = readBody(result);
        assertExactFields(body, "authenticated");
        assertThat(body.get("authenticated").asBoolean()).isFalse();
    }

    @Test
    void registerSetsCrossSiteCookieAndSessionForSecureRequests() throws Exception {
        String email = testEmail("frontend-register");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/frontend/auth/register")
                        .secure(true)
                        .header(HttpHeaders.ORIGIN, FRONTEND_ORIGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new RegisterRequest("Test", "Client", "+49 30 555 9001", email, "secret-123"))))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, FRONTEND_ORIGIN))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"))
                .andReturn();

        JsonNode registerBody = readBody(registerResult);
        assertExactFields(registerBody, "ok", "role");
        assertThat(registerBody.get("ok").asBoolean()).isTrue();
        assertThat(registerBody.get("role").asText()).isEqualTo("client");
        assertThat(registerResult.getResponse().getHeader(HttpHeaders.SET_COOKIE))
                .contains("SALON_AUTH=")
                .contains("HttpOnly")
                .contains("SameSite=None")
                .contains("Secure");

        MvcResult sessionResult = mockMvc.perform(get("/api/v1/frontend/auth/session")
                        .secure(true)
                        .header(HttpHeaders.ORIGIN, FRONTEND_ORIGIN)
                        .cookie(extractAuthCookie(registerResult)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, FRONTEND_ORIGIN))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"))
                .andReturn();

        JsonNode sessionBody = readBody(sessionResult);
        assertExactFields(sessionBody, "authenticated", "role", "user");
        assertThat(sessionBody.get("authenticated").asBoolean()).isTrue();
        assertThat(sessionBody.get("role").asText()).isEqualTo("client");
        assertExactFields(sessionBody.get("user"), "firstName", "lastName", "phone", "email", "role");
        assertThat(sessionBody.get("user").get("email").asText()).isEqualTo(email);
        assertThat(sessionBody.get("user").get("role").asText()).isEqualTo("client");
    }

    @Test
    void registerAllowsBlankPhoneFromFrontendPayload() throws Exception {
        String email = testEmail("frontend-register-blank-phone");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/frontend/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Test",
                                  "lastName": "Client",
                                  "phone": "",
                                  "email": "%s",
                                  "password": "secret-123"
                                }
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode registerBody = readBody(registerResult);
        assertExactFields(registerBody, "ok", "role");
        assertThat(registerBody.get("ok").asBoolean()).isTrue();

        MvcResult sessionResult = mockMvc.perform(get("/api/v1/frontend/auth/session")
                        .cookie(extractAuthCookie(registerResult)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode sessionBody = readBody(sessionResult);
        assertThat(sessionBody.get("authenticated").asBoolean()).isTrue();
        assertThat(sessionBody.get("user").get("phone").asText()).isEmpty();
    }

    @Test
    void registerDuplicateEmailStillMatchesFrontendContract() throws Exception {
        String email = testEmail("frontend-register-duplicate");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/frontend/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new RegisterRequest("Test", "Client", "+49 30 555 9001", email, "secret-123"))))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult duplicateResult = mockMvc.perform(post("/api/v1/frontend/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new RegisterRequest(
                                "Test",
                                "Client",
                                "+49 30 555 9002",
                                email.toUpperCase(Locale.ROOT),
                                "secret-123"))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode duplicateBody = readBody(duplicateResult);
        assertExactFields(duplicateBody, "ok", "error");
        assertThat(duplicateBody.get("ok").asBoolean()).isFalse();
        assertThat(duplicateBody.get("error").asText()).isEqualTo("email_exists");
        assertThat(duplicateResult.getResponse().getHeader(HttpHeaders.SET_COOKIE)).isNull();
        assertThat(extractAuthCookie(registerResult).getValue()).isNotBlank();
    }

    @Test
    void guestLoginSetsCookieAndSession() throws Exception {
        MvcResult guestResult = mockMvc.perform(post("/api/v1/frontend/auth/guest")
                        .secure(true)
                        .header(HttpHeaders.ORIGIN, FRONTEND_ORIGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new GuestRequest("UA"))))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, FRONTEND_ORIGIN))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"))
                .andReturn();

        JsonNode guestBody = readBody(guestResult);
        assertExactFields(guestBody, "ok", "role");
        assertThat(guestBody.get("ok").asBoolean()).isTrue();
        assertThat(guestBody.get("role").asText()).isEqualTo("guest");
        assertThat(guestResult.getResponse().getHeader(HttpHeaders.SET_COOKIE))
                .contains("SALON_AUTH=")
                .contains("SameSite=None")
                .contains("Secure");

        MvcResult sessionResult = mockMvc.perform(get("/api/v1/frontend/auth/session")
                        .secure(true)
                        .header(HttpHeaders.ORIGIN, FRONTEND_ORIGIN)
                        .cookie(extractAuthCookie(guestResult)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode sessionBody = readBody(sessionResult);
        assertExactFields(sessionBody, "authenticated", "role");
        assertThat(sessionBody.get("authenticated").asBoolean()).isTrue();
        assertThat(sessionBody.get("role").asText()).isEqualTo("guest");
    }

    @Test
    void corsPreflightAllowsCredentialedFrontendRequests() throws Exception {
        mockMvc.perform(options("/api/v1/frontend/auth/login")
                        .header(HttpHeaders.ORIGIN, FRONTEND_ORIGIN)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, FRONTEND_ORIGIN))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true"));
    }

    @Test
    void devTokenEndpointIsDisabledByDefault() throws Exception {
        mockMvc.perform(get("/api/v1/auth/dev-token"))
                .andExpect(status().isNotFound());
    }

    @Test
    void loginAndLogoutMatchFrontendContract() throws Exception {
        MvcResult invalidLoginResult = mockMvc.perform(post("/api/v1/frontend/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new LoginRequest(ADMIN_EMAIL, "wrong-password"))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode invalidLoginBody = readBody(invalidLoginResult);
        assertExactFields(invalidLoginBody, "ok", "error");
        assertThat(invalidLoginBody.get("ok").asBoolean()).isFalse();
        assertThat(invalidLoginBody.get("error").asText()).isEqualTo("invalid_credentials");

        MvcResult loginResult = mockMvc.perform(post("/api/v1/frontend/auth/login")
                        .secure(true)
                        .header(HttpHeaders.ORIGIN, FRONTEND_ORIGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new LoginRequest(ADMIN_EMAIL, ADMIN_PASSWORD))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginBody = readBody(loginResult);
        assertExactFields(loginBody, "ok", "role");
        assertThat(loginBody.get("ok").asBoolean()).isTrue();
        assertThat(loginBody.get("role").asText()).isEqualTo("admin");

        MvcResult logoutResult = mockMvc.perform(post("/api/v1/frontend/auth/logout")
                        .secure(true)
                        .header(HttpHeaders.ORIGIN, FRONTEND_ORIGIN)
                        .cookie(extractAuthCookie(loginResult)))
                .andExpect(status().isNoContent())
                .andReturn();

        assertThat(logoutResult.getResponse().getHeader(HttpHeaders.SET_COOKIE))
                .contains("SALON_AUTH=")
                .contains("Max-Age=0")
                .contains("SameSite=None")
                .contains("Secure");

        MvcResult sessionAfterLogout = mockMvc.perform(get("/api/v1/frontend/auth/session")
                        .secure(true)
                        .cookie(extractAuthCookie(logoutResult)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode sessionBody = readBody(sessionAfterLogout);
        assertExactFields(sessionBody, "authenticated");
        assertThat(sessionBody.get("authenticated").asBoolean()).isFalse();
    }
}
