package org.example.salon_project.frontend;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FrontendPublicCatalogApiIntegrationTest extends FrontendIntegrationTestSupport {

    @Test
    void bootstrapWithoutAdminCookieMatchesFrontendShape() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/frontend/bootstrap"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = readBody(result);
        assertExactFields(body, "serviceCategories", "services", "masters", "bookingSlots", "careProducts", "drinks", "trends", "bookings", "careOrders", "drinkOrders");
        assertThat(body.get("serviceCategories").isEmpty()).isFalse();
        assertThat(body.get("services").isEmpty()).isFalse();
        assertThat(body.get("masters").isEmpty()).isFalse();
        assertThat(body.get("bookingSlots").isEmpty()).isFalse();
        assertThat(body.get("careProducts").isEmpty()).isFalse();
        assertThat(body.get("drinks").isEmpty()).isFalse();
        assertThat(body.get("trends").isEmpty()).isFalse();
        assertThat(body.get("bookings").isEmpty()).isTrue();
        assertThat(body.get("careOrders").isEmpty()).isTrue();
        assertThat(body.get("drinkOrders").isEmpty()).isTrue();

        JsonNode category = body.get("serviceCategories").get(0);
        assertExactFields(category, "id", "slug", "icon", "title", "description");
        assertLocalizedTextShape(category.get("title"));
        assertLocalizedTextShape(category.get("description"));

        JsonNode service = body.get("services").get(0);
        assertExactFields(service, "id", "categoryId", "title", "durationMinutes", "price");
        assertLocalizedTextShape(service.get("title"));

        JsonNode master = body.get("masters").get(0);
        assertExactFields(master, "id", "name", "role", "initials", "experienceLabel", "specialtyCategoryIds", "credentials");
        assertLocalizedTextShape(master.get("role"));
        assertThat(master.get("specialtyCategoryIds").isEmpty()).isFalse();
        assertThat(master.get("credentials").isArray()).isTrue();

        JsonNode bookingSlot = body.get("bookingSlots").get(0);
        assertExactFields(bookingSlot, "id", "period", "time");

        JsonNode careProduct = body.get("careProducts").get(0);
        assertExactFields(careProduct, "id", "title", "brand", "price", "icon");
        assertLocalizedTextShape(careProduct.get("title"));

        JsonNode drink = body.get("drinks").get(0);
        assertExactFields(drink, "id", "title", "icon");
        assertLocalizedTextShape(drink.get("title"));

        JsonNode trend = body.get("trends").get(0);
        assertExactFields(trend, "id", "title", "description", "gradient", "emoji");
        assertLocalizedTextShape(trend.get("title"));
        assertLocalizedTextShape(trend.get("description"));
    }

    @Test
    void publicCatalogEndpointsReturnFrontendShapedArrays() throws Exception {
        assertListEndpoint("/api/v1/frontend/service-categories", "id", "slug", "icon", "title", "description");
        assertListEndpoint("/api/v1/frontend/services", "id", "categoryId", "title", "durationMinutes", "price");
        assertListEndpoint("/api/v1/frontend/masters", "id", "name", "role", "initials", "experienceLabel", "specialtyCategoryIds", "credentials");
        assertListEndpoint("/api/v1/frontend/booking-slots", "id", "period", "time");
        assertListEndpoint("/api/v1/frontend/care-products", "id", "title", "brand", "price", "icon");
        assertListEndpoint("/api/v1/frontend/drinks", "id", "title", "icon");
        assertListEndpoint("/api/v1/frontend/trends", "id", "title", "description", "gradient", "emoji");
    }

    @Test
    void bootstrapIncludesBookingsForAdmin() throws Exception {
        Cookie adminCookie = loginAsAdmin();

        MvcResult result = mockMvc.perform(get("/api/v1/frontend/bootstrap").cookie(adminCookie))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = readBody(result);
        assertExactFields(body, "serviceCategories", "services", "masters", "bookingSlots", "careProducts", "drinks", "trends", "bookings", "careOrders", "drinkOrders");
        assertThat(body.get("bookings").isEmpty()).isFalse();

        JsonNode booking = body.get("bookings").get(0);
        assertExactFields(booking, "id", "customer", "categoryId", "serviceId", "masterId", "date", "time", "status", "note", "createdAt");
        assertExactFields(booking.get("customer"), "firstName", "lastName", "phone", "email");
    }

    private void assertListEndpoint(String path, String... expectedFields) throws Exception {
        MvcResult result = mockMvc.perform(get(path))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = readBody(result);
        assertThat(body.isEmpty()).isFalse();
        assertExactFields(body.get(0), expectedFields);
    }
}
