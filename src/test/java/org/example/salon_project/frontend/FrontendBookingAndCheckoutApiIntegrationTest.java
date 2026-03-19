package org.example.salon_project.frontend;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.Cookie;
import org.example.salon_project.frontend.dto.BookingCustomerDto;
import org.example.salon_project.frontend.dto.BookingSaveRequest;
import org.example.salon_project.frontend.dto.CartItemDto;
import org.example.salon_project.frontend.dto.CareProductCheckoutRequest;
import org.example.salon_project.frontend.dto.DrinkOrderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FrontendBookingAndCheckoutApiIntegrationTest extends FrontendIntegrationTestSupport {

    @Test
    void bookingsCreateUpdateDeleteAndProtectionMatchFrontendContract() throws Exception {
        mockMvc.perform(get("/api/v1/frontend/bookings"))
                .andExpect(status().isUnauthorized());

        String email = testEmail("booking");
        Cookie clientCookie = registerClient("Book", "Client", "+49 30 555 9911", email, "secret-123");
        Cookie adminCookie = loginAsAdmin();
        String bookingId = testExternalId("booking");
        String bookingDate = randomFutureDate();

        BookingSaveRequest createRequest = new BookingSaveRequest(
                bookingId,
                new BookingCustomerDto("Book", "Client", "+49 30 555 9911", email),
                "cat-hair",
                "srv-signature-cut",
                "master-anna",
                bookingDate,
                "19:05",
                null,
                "Prefers a soft finish.",
                null);

        MvcResult createResult = mockMvc.perform(post("/api/v1/frontend/bookings")
                        .cookie(clientCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(createRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode createBody = readBody(createResult);
        assertExactFields(createBody, "id", "customer", "categoryId", "serviceId", "masterId", "date", "time", "status", "note", "createdAt");
        assertExactFields(createBody.get("customer"), "firstName", "lastName", "phone", "email");
        assertThat(createBody.get("id").asText()).isEqualTo(bookingId);
        assertThat(createBody.get("status").asText()).isEqualTo("new");
        assertThat(createBody.get("date").asText()).isEqualTo(bookingDate);
        assertThat(createBody.get("time").asText()).isEqualTo("19:05");
        assertThat(createBody.get("createdAt").asText()).isNotBlank();

        var savedBooking = bookingRepository.findByExternalId(bookingId).orElseThrow();
        var expectedClient = clientRepository.findByEmailIgnoreCase(email).orElseThrow();
        assertThat(savedBooking.getClient()).isNotNull();
        assertThat(savedBooking.getClient().getId()).isEqualTo(expectedClient.getId());
        assertThat(savedBooking.getCustomerEmail()).isEqualTo(email);
        assertThat(savedBooking.getCategory()).isNotNull();
        assertThat(savedBooking.getService()).isNotNull();
        assertThat(savedBooking.getMaster()).isNotNull();

        BookingSaveRequest updateRequest = new BookingSaveRequest(
                null,
                new BookingCustomerDto("Book", "Client", "+49 30 555 9911", email),
                "cat-hair",
                "srv-signature-cut",
                "master-anna",
                bookingDate,
                "19:05",
                "confirmed",
                "Confirmed by admin.",
                null);

        MvcResult updateResult = mockMvc.perform(put("/api/v1/frontend/bookings/{id}", bookingId)
                        .cookie(adminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(updateRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode updateBody = readBody(updateResult);
        assertThat(updateBody.get("status").asText()).isEqualTo("confirmed");
        assertThat(updateBody.get("note").asText()).isEqualTo("Confirmed by admin.");

        MvcResult listResult = mockMvc.perform(get("/api/v1/frontend/bookings").cookie(adminCookie))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode listBody = readBody(listResult);
        assertThat(listBody.isArray()).isTrue();
        boolean containsBooking = false;
        for (JsonNode node : listBody) {
            if (bookingId.equals(node.get("id").asText())) {
                containsBooking = true;
                break;
            }
        }
        assertThat(containsBooking).isTrue();

        mockMvc.perform(delete("/api/v1/frontend/bookings/{id}", bookingId).cookie(adminCookie))
                .andExpect(status().isOk());
        assertThat(bookingRepository.findByExternalId(bookingId)).isEmpty();
    }

    @Test
    void bookingValidationRejectsCategoryServiceMasterAndConflictMismatches() throws Exception {
        BookingSaveRequest wrongServiceRequest = new BookingSaveRequest(
                testExternalId("booking-invalid-service"),
                new BookingCustomerDto("Sofia", "Mismatch", "+49 30 555 0001", "sofia.mismatch@example.com"),
                "cat-hair",
                "srv-event-glam",
                "master-anna",
                randomFutureDate(),
                "18:35",
                null,
                "",
                null);

        JsonNode wrongServiceBody = readBody(mockMvc.perform(post("/api/v1/frontend/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(wrongServiceRequest)))
                .andExpect(status().isBadRequest())
                .andReturn());
        assertExactFields(wrongServiceBody, "code", "message", "timestamp");
        assertThat(wrongServiceBody.get("code").asText()).isEqualTo("VALIDATION_ERROR");
        assertThat(wrongServiceBody.get("message").asText()).isEqualTo("serviceId does not belong to categoryId");

        BookingSaveRequest wrongMasterRequest = new BookingSaveRequest(
                testExternalId("booking-invalid-master"),
                new BookingCustomerDto("Sofia", "Mismatch", "+49 30 555 0002", "master.mismatch@example.com"),
                "cat-hair",
                "srv-signature-cut",
                "master-olena",
                randomFutureDate(),
                "18:40",
                null,
                "",
                null);

        JsonNode wrongMasterBody = readBody(mockMvc.perform(post("/api/v1/frontend/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(wrongMasterRequest)))
                .andExpect(status().isBadRequest())
                .andReturn());
        assertThat(wrongMasterBody.get("message").asText()).isEqualTo("masterId is not available for categoryId");

        BookingSaveRequest conflictRequest = new BookingSaveRequest(
                testExternalId("booking-conflict"),
                new BookingCustomerDto("Sofia", "Conflict", "+49 30 555 0003", "conflict@example.com"),
                "cat-hair",
                "srv-gloss-color",
                "master-maria",
                "2026-03-14",
                "12:30",
                null,
                "",
                null);

        JsonNode conflictBody = readBody(mockMvc.perform(post("/api/v1/frontend/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(conflictRequest)))
                .andExpect(status().isBadRequest())
                .andReturn());
        assertThat(conflictBody.get("message").asText()).isEqualTo("The selected master already has a booking at this date and time");
    }

    @Test
    void careProductCheckoutMatchesFrontendShapeAndUsesCatalogPrices() throws Exception {
        CareProductCheckoutRequest request = new CareProductCheckoutRequest(List.of(
                new CartItemDto("prod-gold-shampoo", "Gold Recovery Shampoo", BigDecimal.valueOf(999), 2),
                new CartItemDto("prod-argan-serum", "Argan Shine Serum", BigDecimal.ONE, 1)
        ));

        MvcResult result = mockMvc.perform(post("/api/v1/frontend/care-product-checkouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = readBody(result);
        assertExactFields(body, "ordered", "id", "items", "total", "createdAt");
        assertThat(body.get("ordered").asBoolean()).isTrue();
        assertThat(body.get("id").asText()).isNotBlank();
        assertThat(body.get("items").size()).isEqualTo(2);
        assertExactFields(body.get("items").get(0), "id", "name", "price", "quantity");
        assertThat(body.get("items").get(0).get("price").decimalValue()).isEqualByComparingTo("29");
        assertThat(body.get("items").get(1).get("price").decimalValue()).isEqualByComparingTo("38");
        assertThat(body.get("total").decimalValue()).isEqualByComparingTo("96");
        assertThat(body.get("createdAt").asText()).isNotBlank();

        JsonNode errorBody = readBody(mockMvc.perform(post("/api/v1/frontend/care-product-checkouts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new CareProductCheckoutRequest(List.of(
                                new CartItemDto("missing-product", "Missing", BigDecimal.TEN, 1)
                        )))))
                .andExpect(status().isBadRequest())
                .andReturn());

        assertThat(errorBody.get("code").asText()).isEqualTo("VALIDATION_ERROR");
        assertThat(errorBody.get("message").asText()).isEqualTo("Unknown care product: missing-product");
    }

    @Test
    void drinkOrderMatchesFrontendShapeAndRejectsUnknownDrinks() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/frontend/drink-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new DrinkOrderRequest("drink-espresso"))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = readBody(result);
        assertExactFields(body, "ordered", "id", "drinkId", "createdAt");
        assertThat(body.get("ordered").asBoolean()).isTrue();
        assertThat(body.get("id").asText()).isNotBlank();
        assertThat(body.get("drinkId").asText()).isEqualTo("drink-espresso");
        assertThat(body.get("createdAt").asText()).isNotBlank();

        JsonNode errorBody = readBody(mockMvc.perform(post("/api/v1/frontend/drink-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new DrinkOrderRequest("missing-drink"))))
                .andExpect(status().isBadRequest())
                .andReturn());

        assertThat(errorBody.get("code").asText()).isEqualTo("VALIDATION_ERROR");
        assertThat(errorBody.get("message").asText()).isEqualTo("Unknown drink: missing-drink");
    }

    @Test
    void authenticatedClientCanCancelOwnFrontendOrders() throws Exception {
        String email = testEmail("orders");
        Cookie clientCookie = registerClient("Order", "Client", "+49 30 555 3311", email, "secret-123");

        JsonNode careOrderBody = readBody(mockMvc.perform(post("/api/v1/frontend/care-product-checkouts")
                        .cookie(clientCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new CareProductCheckoutRequest(List.of(
                                new CartItemDto("prod-gold-shampoo", "Gold Recovery Shampoo", BigDecimal.ZERO, 1)
                        )))))
                .andExpect(status().isOk())
                .andReturn());
        assertThat(careOrderBody.get("email").asText()).isEqualTo(email);
        assertThat(careOrderBody.get("firstName").asText()).isEqualTo("Order");
        assertThat(careOrderBody.get("lastName").asText()).isEqualTo("Client");
        assertThat(careOrderBody.get("phone").asText()).isEqualTo("+49 30 555 3311");

        JsonNode drinkOrderBody = readBody(mockMvc.perform(post("/api/v1/frontend/drink-orders")
                        .cookie(clientCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new DrinkOrderRequest("drink-espresso"))))
                .andExpect(status().isOk())
                .andReturn());

        String careOrderId = careOrderBody.get("id").asText();
        String drinkOrderId = drinkOrderBody.get("id").asText();

        JsonNode bootstrapBeforeCancel = readBody(mockMvc.perform(get("/api/v1/frontend/bootstrap").cookie(clientCookie))
                .andExpect(status().isOk())
                .andReturn());
        assertThat(bootstrapBeforeCancel.get("careOrders").size()).isEqualTo(1);
        assertThat(bootstrapBeforeCancel.get("careOrders").get(0).get("firstName").asText()).isEqualTo("Order");
        assertThat(bootstrapBeforeCancel.get("careOrders").get(0).get("lastName").asText()).isEqualTo("Client");
        assertThat(bootstrapBeforeCancel.get("careOrders").get(0).get("phone").asText()).isEqualTo("+49 30 555 3311");

        mockMvc.perform(delete("/api/v1/frontend/care-product-checkouts/{id}", careOrderId).cookie(clientCookie))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/v1/frontend/drink-orders/{id}", drinkOrderId).cookie(clientCookie))
                .andExpect(status().isOk());

        JsonNode bootstrapBody = readBody(mockMvc.perform(get("/api/v1/frontend/bootstrap").cookie(clientCookie))
                .andExpect(status().isOk())
                .andReturn());

        assertThat(bootstrapBody.get("careOrders").isEmpty()).isTrue();
        assertThat(bootstrapBody.get("drinkOrders").isEmpty()).isTrue();
    }

    private String randomFutureDate() {
        int offsetDays = 45 + Math.floorMod(UUID.randomUUID().hashCode(), 240);
        return LocalDate.now().plusDays(offsetDays).toString();
    }
}
