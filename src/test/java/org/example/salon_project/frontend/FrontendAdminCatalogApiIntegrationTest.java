package org.example.salon_project.frontend;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.Cookie;
import org.example.salon_project.frontend.dto.BookingSlotSaveRequest;
import org.example.salon_project.frontend.dto.CareProductSaveRequest;
import org.example.salon_project.frontend.dto.DrinkItemSaveRequest;
import org.example.salon_project.frontend.dto.LocalizedTextDto;
import org.example.salon_project.frontend.dto.MasterCredentialSaveRequest;
import org.example.salon_project.frontend.dto.MasterSaveRequest;
import org.example.salon_project.frontend.dto.ServiceCategorySaveRequest;
import org.example.salon_project.frontend.dto.ServiceItemSaveRequest;
import org.example.salon_project.frontend.dto.TrendSaveRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FrontendAdminCatalogApiIntegrationTest extends FrontendIntegrationTestSupport {

    @Test
    void catalogAdminMutationEndpointsRequireAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/frontend/service-categories").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/v1/frontend/service-categories/test-category").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/v1/frontend/service-categories/test-category"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/frontend/services").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/v1/frontend/services/test-service").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/v1/frontend/services/test-service"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/frontend/masters").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/v1/frontend/masters/test-master").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/v1/frontend/masters/test-master"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/frontend/booking-slots").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/v1/frontend/booking-slots/test-slot").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/v1/frontend/booking-slots/test-slot"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/frontend/care-products").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/v1/frontend/care-products/test-product").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/v1/frontend/care-products/test-product"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/frontend/drinks").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/v1/frontend/drinks/test-drink").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/v1/frontend/drinks/test-drink"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/frontend/trends").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(put("/api/v1/frontend/trends/test-trend").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/v1/frontend/trends/test-trend"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void serviceCategoriesCrudMatchesFrontendShape() throws Exception {
        Cookie adminCookie = loginAsAdmin();
        String categoryId = testExternalId("category");
        String slug = "test-slug-" + categoryId.substring(categoryId.length() - 8);

        MvcResult createResult = mockMvc.perform(post("/api/v1/frontend/service-categories")
                        .cookie(adminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new ServiceCategorySaveRequest(
                                categoryId,
                                slug,
                                "S",
                                new LocalizedTextDto("Category UA", "Kategorie", "Category"),
                                new LocalizedTextDto("Description UA", "Beschreibung", "Description")))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode createBody = readBody(createResult);
        assertExactFields(createBody, "id", "slug", "icon", "title", "description");
        assertThat(createBody.get("id").asText()).isEqualTo(categoryId);
        assertThat(createBody.get("slug").asText()).isEqualTo(slug);
        assertLocalizedTextShape(createBody.get("title"));

        MvcResult updateResult = mockMvc.perform(put("/api/v1/frontend/service-categories/{id}", categoryId)
                        .cookie(adminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new ServiceCategorySaveRequest(
                                null,
                                slug,
                                "T",
                                new LocalizedTextDto("Category UA 2", "Kategorie 2", "Category 2"),
                                new LocalizedTextDto("Description UA 2", "Beschreibung 2", "Description 2")))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode updateBody = readBody(updateResult);
        assertThat(updateBody.get("title").get("GB").asText()).isEqualTo("Category 2");

        mockMvc.perform(delete("/api/v1/frontend/service-categories/{id}", categoryId).cookie(adminCookie))
                .andExpect(status().isOk());
        assertThat(categoryRepository.findByExternalId(categoryId)).isEmpty();
    }

    @Test
    void servicesCrudMatchesFrontendShape() throws Exception {
        Cookie adminCookie = loginAsAdmin();
        String serviceId = testExternalId("service");

        MvcResult createResult = mockMvc.perform(post("/api/v1/frontend/services")
                        .cookie(adminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new ServiceItemSaveRequest(
                                serviceId,
                                "cat-hair",
                                new LocalizedTextDto("Service UA", "Test", "Test Service"),
                                70,
                                BigDecimal.valueOf(77)))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode createBody = readBody(createResult);
        assertExactFields(createBody, "id", "categoryId", "title", "durationMinutes", "price");
        assertThat(createBody.get("id").asText()).isEqualTo(serviceId);
        assertThat(createBody.get("categoryId").asText()).isEqualTo("cat-hair");
        assertThat(createBody.get("durationMinutes").asInt()).isEqualTo(70);

        MvcResult updateResult = mockMvc.perform(put("/api/v1/frontend/services/{id}", serviceId)
                        .cookie(adminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new ServiceItemSaveRequest(
                                null,
                                "cat-hair",
                                new LocalizedTextDto("Service UA 2", "Test 2", "Test Service 2"),
                                80,
                                BigDecimal.valueOf(88)))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode updateBody = readBody(updateResult);
        assertThat(updateBody.get("title").get("GB").asText()).isEqualTo("Test Service 2");
        assertThat(updateBody.get("price").decimalValue()).isEqualByComparingTo("88");

        mockMvc.perform(delete("/api/v1/frontend/services/{id}", serviceId).cookie(adminCookie))
                .andExpect(status().isOk());
        assertThat(beautyServiceRepository.findByExternalId(serviceId)).isEmpty();
    }

    @Test
    void mastersCrudMatchesFrontendShape() throws Exception {
        Cookie adminCookie = loginAsAdmin();
        String masterId = testExternalId("master");

        MvcResult createResult = mockMvc.perform(post("/api/v1/frontend/masters")
                        .cookie(adminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new MasterSaveRequest(
                                masterId,
                                "Test Artist",
                                new LocalizedTextDto("Master UA", "Artist", "Artist"),
                                "TA",
                                "9+ years",
                                List.of("cat-hair", "cat-styling"),
                                List.of(new MasterCredentialSaveRequest(
                                        "credential-create",
                                        "Hair Academy",
                                        "application/pdf",
                                        "https://example.com/hair-academy.pdf"))))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode createBody = readBody(createResult);
        assertExactFields(createBody, "id", "name", "role", "initials", "experienceLabel", "specialtyCategoryIds", "credentials");
        assertThat(createBody.get("id").asText()).isEqualTo(masterId);
        assertThat(createBody.get("name").asText()).isEqualTo("Test Artist");
        assertThat(createBody.get("credentials").size()).isEqualTo(1);

        MvcResult updateResult = mockMvc.perform(put("/api/v1/frontend/masters/{id}", masterId)
                        .cookie(adminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new MasterSaveRequest(
                                null,
                                "Test Artist Updated",
                                new LocalizedTextDto("Master UA 2", "Artist 2", "Artist 2"),
                                "TU",
                                "10+ years",
                                List.of("cat-hair"),
                                List.of(new MasterCredentialSaveRequest(
                                        "credential-update",
                                        "Colour Lab",
                                        "image/png",
                                        "https://example.com/colour-lab.png"))))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode updateBody = readBody(updateResult);
        assertThat(updateBody.get("name").asText()).isEqualTo("Test Artist Updated");
        assertThat(updateBody.get("specialtyCategoryIds").size()).isEqualTo(1);
        assertThat(updateBody.get("credentials").size()).isEqualTo(1);

        mockMvc.perform(delete("/api/v1/frontend/masters/{id}", masterId).cookie(adminCookie))
                .andExpect(status().isOk());
        assertThat(masterRepository.findByExternalId(masterId)).isEmpty();
    }

    @Test
    void bookingSlotsCrudMatchesFrontendShape() throws Exception {
        Cookie adminCookie = loginAsAdmin();
        String slotId = testExternalId("slot");

        MvcResult createResult = mockMvc.perform(post("/api/v1/frontend/booking-slots")
                        .cookie(adminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new BookingSlotSaveRequest(slotId, "evening", "19:15"))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode createBody = readBody(createResult);
        assertExactFields(createBody, "id", "period", "time");
        assertThat(createBody.get("id").asText()).isEqualTo(slotId);

        MvcResult updateResult = mockMvc.perform(put("/api/v1/frontend/booking-slots/{id}", slotId)
                        .cookie(adminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new BookingSlotSaveRequest(null, "morning", "08:45"))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode updateBody = readBody(updateResult);
        assertThat(updateBody.get("period").asText()).isEqualTo("morning");
        assertThat(updateBody.get("time").asText()).isEqualTo("08:45");

        mockMvc.perform(delete("/api/v1/frontend/booking-slots/{id}", slotId).cookie(adminCookie))
                .andExpect(status().isOk());
        assertThat(bookingSlotRepository.findByExternalId(slotId)).isEmpty();
    }

    @Test
    void careProductsCrudMatchesFrontendShape() throws Exception {
        Cookie adminCookie = loginAsAdmin();
        String productId = testExternalId("product");

        MvcResult createResult = mockMvc.perform(post("/api/v1/frontend/care-products")
                        .cookie(adminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new CareProductSaveRequest(
                                productId,
                                new LocalizedTextDto("Mask UA", "Maske", "Mask"),
                                "Test Brand",
                                BigDecimal.valueOf(41),
                                "*"))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode createBody = readBody(createResult);
        assertExactFields(createBody, "id", "title", "brand", "price", "icon");
        assertThat(createBody.get("id").asText()).isEqualTo(productId);

        MvcResult updateResult = mockMvc.perform(put("/api/v1/frontend/care-products/{id}", productId)
                        .cookie(adminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new CareProductSaveRequest(
                                null,
                                new LocalizedTextDto("Mask UA 2", "Maske 2", "Mask 2"),
                                "Updated Brand",
                                BigDecimal.valueOf(52),
                                "#"))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode updateBody = readBody(updateResult);
        assertThat(updateBody.get("brand").asText()).isEqualTo("Updated Brand");
        assertThat(updateBody.get("price").decimalValue()).isEqualByComparingTo("52");

        mockMvc.perform(delete("/api/v1/frontend/care-products/{id}", productId).cookie(adminCookie))
                .andExpect(status().isOk());
        assertThat(productRepository.findByExternalId(productId)).isEmpty();
    }

    @Test
    void drinksCrudMatchesFrontendShape() throws Exception {
        Cookie adminCookie = loginAsAdmin();
        String drinkId = testExternalId("drink");

        MvcResult createResult = mockMvc.perform(post("/api/v1/frontend/drinks")
                        .cookie(adminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new DrinkItemSaveRequest(
                                drinkId,
                                new LocalizedTextDto("Tea UA", "Tee", "Tea"),
                                "!"))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode createBody = readBody(createResult);
        assertExactFields(createBody, "id", "title", "icon");
        assertThat(createBody.get("id").asText()).isEqualTo(drinkId);

        MvcResult updateResult = mockMvc.perform(put("/api/v1/frontend/drinks/{id}", drinkId)
                        .cookie(adminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new DrinkItemSaveRequest(
                                null,
                                new LocalizedTextDto("Tea UA 2", "Tee 2", "Tea 2"),
                                "?"))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode updateBody = readBody(updateResult);
        assertThat(updateBody.get("title").get("GB").asText()).isEqualTo("Tea 2");

        mockMvc.perform(delete("/api/v1/frontend/drinks/{id}", drinkId).cookie(adminCookie))
                .andExpect(status().isOk());
        assertThat(drinkRepository.findByExternalId(drinkId)).isEmpty();
    }

    @Test
    void trendsCrudMatchesFrontendShape() throws Exception {
        Cookie adminCookie = loginAsAdmin();
        String trendId = testExternalId("trend");

        MvcResult createResult = mockMvc.perform(post("/api/v1/frontend/trends")
                        .cookie(adminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new TrendSaveRequest(
                                trendId,
                                new LocalizedTextDto("Trend UA", "Trend", "Trend"),
                                new LocalizedTextDto("Description UA", "Beschreibung", "Description"),
                                "linear-gradient(135deg, #111 0%, #222 100%)",
                                "$",
                                null))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode createBody = readBody(createResult);
        assertExactFields(createBody, "id", "title", "description", "gradient", "emoji");
        assertThat(createBody.get("id").asText()).isEqualTo(trendId);

        MvcResult updateResult = mockMvc.perform(put("/api/v1/frontend/trends/{id}", trendId)
                        .cookie(adminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new TrendSaveRequest(
                                null,
                                new LocalizedTextDto("Trend UA 2", "Trend 2", "Trend 2"),
                                new LocalizedTextDto("Description UA 2", "Beschreibung 2", "Description 2"),
                                "linear-gradient(135deg, #333 0%, #444 100%)",
                                "%",
                                null))))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode updateBody = readBody(updateResult);
        assertThat(updateBody.get("emoji").asText()).isEqualTo("%");
        assertThat(updateBody.get("gradient").asText()).contains("#333");

        mockMvc.perform(delete("/api/v1/frontend/trends/{id}", trendId).cookie(adminCookie))
                .andExpect(status().isOk());
        assertThat(trendRepository.findByExternalId(trendId)).isEmpty();
    }
}
