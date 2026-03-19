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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FrontendAuthApiIntegrationTest extends FrontendIntegrationTestSupport {

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
    void registerDuplicateEmailAndSessionMatchFrontendContract() throws Exception {
        String email = testEmail("frontend-register");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/frontend/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new RegisterRequest("Test", "Client", "+49 30 555 9001", email, "secret-123"))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode registerBody = readBody(registerResult);
        assertExactFields(registerBody, "ok", "role");
        assertThat(registerBody.get("ok").asBoolean()).isTrue();
        assertThat(registerBody.get("role").asText()).isEqualTo("client");
        assertThat(registerResult.getResponse().getHeader(HttpHeaders.SET_COOKIE))
                .contains("SALON_AUTH=")
                .contains("HttpOnly")
                .contains("SameSite=Lax");

        MvcResult sessionResult = mockMvc.perform(get("/api/v1/frontend/auth/session")
                        .cookie(extractAuthCookie(registerResult)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode sessionBody = readBody(sessionResult);
        assertExactFields(sessionBody, "authenticated", "role", "user");
        assertThat(sessionBody.get("authenticated").asBoolean()).isTrue();
        assertThat(sessionBody.get("role").asText()).isEqualTo("client");
        assertExactFields(sessionBody.get("user"), "firstName", "lastName", "phone", "email", "role");
        assertThat(sessionBody.get("user").get("email").asText()).isEqualTo(email);
        assertThat(sessionBody.get("user").get("role").asText()).isEqualTo("client");

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
    }

    @Test
    void loginGuestAndLogoutMatchFrontendContract() throws Exception {
        MvcResult invalidLoginResult = mockMvc.perform(post("/api/v1/frontend/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new LoginRequest(ADMIN_EMAIL, "wrong-password"))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode invalidLoginBody = readBody(invalidLoginResult);
        assertExactFields(invalidLoginBody, "ok", "error");
        assertThat(invalidLoginBody.get("ok").asBoolean()).isFalse();
        assertThat(invalidLoginBody.get("error").asText()).isEqualTo("invalid_credentials");

        MvcResult guestResult = mockMvc.perform(post("/api/v1/frontend/auth/guest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new GuestRequest("UA"))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode guestBody = readBody(guestResult);
        assertExactFields(guestBody, "ok", "role");
        assertThat(guestBody.get("ok").asBoolean()).isTrue();
        assertThat(guestBody.get("role").asText()).isEqualTo("guest");
        assertThat(guestResult.getResponse().getHeader(HttpHeaders.SET_COOKIE)).isNull();

        MvcResult loginResult = mockMvc.perform(post("/api/v1/frontend/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new LoginRequest(ADMIN_EMAIL, ADMIN_PASSWORD))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode loginBody = readBody(loginResult);
        assertExactFields(loginBody, "ok", "role");
        assertThat(loginBody.get("ok").asBoolean()).isTrue();
        assertThat(loginBody.get("role").asText()).isEqualTo("admin");

        MvcResult logoutResult = mockMvc.perform(post("/api/v1/frontend/auth/logout")
                        .cookie(extractAuthCookie(loginResult)))
                .andExpect(status().isNoContent())
                .andReturn();

        assertThat(logoutResult.getResponse().getHeader(HttpHeaders.SET_COOKIE))
                .contains("SALON_AUTH=")
                .contains("Max-Age=0");

        MvcResult sessionAfterLogout = mockMvc.perform(get("/api/v1/frontend/auth/session")
                        .cookie(extractAuthCookie(logoutResult)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode sessionBody = readBody(sessionAfterLogout);
        assertExactFields(sessionBody, "authenticated");
        assertThat(sessionBody.get("authenticated").asBoolean()).isFalse();
    }
}
