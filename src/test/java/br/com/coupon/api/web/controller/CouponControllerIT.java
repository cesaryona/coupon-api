package br.com.coupon.api.web.controller;

import br.com.coupon.api.domain.model.CouponStatus;
import br.com.coupon.api.infra.persistence.CouponEntity;
import br.com.coupon.api.infra.persistence.CouponJpaRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CouponControllerIT {

    private static final String URL = "/coupon";
    private static final LocalDateTime DATE = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
    private static final LocalDateTime PAST_DATE = LocalDateTime.now().minusDays(1);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CouponJpaRepository jpaRepository;

    @Nested
    class CreateCoupon {

        @Test
        void shouldCreateCoupon() throws Exception {
            mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                            .content(body("ABC123", "10", DATE)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.code").value("ABC123"))
                    .andExpect(jsonPath("$.description").value("Cupom 100"))
                    .andExpect(jsonPath("$.discountValue").value(10))
                    .andExpect(jsonPath("$.expirationDate").value(DATE.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                    .andExpect(jsonPath("$.status").value("ACTIVE"))
                    .andExpect(jsonPath("$.published").value(false))
                    .andExpect(jsonPath("$.redeemed").value(false));
        }

        @Test
        void shouldSanitizeCode() throws Exception {
            mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                            .content(body("ABC-123", "10", DATE)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value("ABC123"));
        }

        @Test
        void shouldCreatePublishedCoupon() throws Exception {
            mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "code": "ABC123",
                                      "description": "Cupom de teste",
                                      "discountValue": 10,
                                      "expirationDate": "%s",
                                      "published": true
                                    }
                                    """.formatted(DATE)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.published").value(true));
        }

        @Test
        void shouldRejectMissingFields() throws Exception {
            mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.length()").value(4));
        }

        @Test
        void shouldRejectInvalidCode() throws Exception {
            mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                            .content(body("AB-12", "10", DATE)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldRejectDiscountBelowMinimum() throws Exception {
            mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                            .content(body("ABC123", "0.49", DATE)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldRejectExpirationDateInPast() throws Exception {
            mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                            .content(body("ABC123", "10", PAST_DATE)))
                    .andExpect(status().isBadRequest());
        }

    }

    @Nested
    class FindCoupon {

        @Test
        void shouldFindCoupon() throws Exception {
            UUID id = createCoupon();

            mockMvc.perform(get(URL + "/" + id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.code").value("ABC123"))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        void shouldReturnNotFoundForUnknownId() throws Exception {
            mockMvc.perform(get(URL + "/" + UUID.randomUUID()))
                    .andExpect(status().isNotFound());
        }

    }

    @Nested
    class DeleteCoupon {

        @Test
        void shouldDeleteCoupon() throws Exception {
            UUID id = createCoupon();

            mockMvc.perform(delete(URL + "/" + id))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldKeepDataAfterDelete() throws Exception {
            UUID id = createCoupon();

            mockMvc.perform(delete(URL + "/" + id));

            CouponEntity entity = jpaRepository.findById(id).orElseThrow();
            assertEquals(CouponStatus.DELETED, entity.getStatus());
            assertNotNull(entity.getDeletedAt());
            assertEquals("ABC123", entity.getCode());
            assertEquals("Cupom 100", entity.getDescription());
            assertEquals(0, new BigDecimal("10").compareTo(entity.getDiscountValue()));
            assertEquals(DATE, entity.getExpirationDate());
        }

        @Test
        void shouldNotDeleteTwice() throws Exception {
            UUID id = createCoupon();
            mockMvc.perform(delete(URL + "/" + id));

            mockMvc.perform(delete(URL + "/" + id))
                    .andExpect(status().isConflict());
        }

    }

    private UUID createCoupon() throws Exception {
        String location = mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(body("ABC-123", "10", DATE)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
        return UUID.fromString(location.substring(location.lastIndexOf('/') + 1));
    }

    private static String body(String code, String discountValue, LocalDateTime expirationDate) {
        return """
                {
                  "code": "%s",
                  "description": "Cupom 100",
                  "discountValue": %s,
                  "expirationDate": "%s"
                }
                """.formatted(code, discountValue, expirationDate);
    }
}
