package org.example.salon_project.frontend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class FrontendInfrastructureIntegrationTest extends FrontendIntegrationTestSupport {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void liquibaseDatabaseConnectionAndRepositoriesAreReady() {
        Integer appliedChangeSets = jdbcTemplate.queryForObject(
                "select count(*) from databasechangelog where id in ('001-create-schema', '002-frontend-contract')",
                Integer.class);
        Integer salonBookingStatusColumn = jdbcTemplate.queryForObject(
                """
                        select count(*)
                        from information_schema.columns
                        where table_name = 'salon_bookings'
                          and column_name = 'status'
                        """,
                Integer.class);

        assertThat(appliedChangeSets).isEqualTo(2);
        assertThat(salonBookingStatusColumn).isEqualTo(1);

        assertThat(clientRepository.findByEmailIgnoreCase(ADMIN_EMAIL)).isPresent();
        assertThat(categoryRepository.findByExternalIdIsNotNullOrderBySortOrderAsc())
                .extracting(category -> category.getExternalId())
                .contains("cat-hair", "cat-nails", "cat-makeup", "cat-styling");
        assertThat(beautyServiceRepository.findByExternalId("srv-signature-cut")).isPresent();
        assertThat(masterRepository.findByExternalId("master-anna")).isPresent();
        assertThat(bookingSlotRepository.findByExternalId("slot-0900")).isPresent();
        assertThat(productRepository.findByExternalId("prod-gold-shampoo")).isPresent();
        assertThat(drinkRepository.findByExternalId("drink-espresso")).isPresent();
        assertThat(trendRepository.findByExternalId("trend-balayage")).isPresent();
        assertThat(bookingRepository.findByExternalId("booking-seed-1")).isPresent();
    }
}
