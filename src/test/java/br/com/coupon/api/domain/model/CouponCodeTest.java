package br.com.coupon.api.domain.model;

import br.com.coupon.api.domain.exception.InvalidCouponCodeException;
import br.com.coupon.api.domain.exception.RequiredFieldException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CouponCodeTest {

    @Test
    void shouldRemoveSpecialCharacters() {
        CouponCode code = CouponCode.sanitize("ABC-123");

        assertEquals("ABC123", code.value());
    }

    @Test
    void shouldRejectCodeWithoutSixCharactersAfterSanitize() {
        assertThrows(InvalidCouponCodeException.class, () -> CouponCode.sanitize("AB-12"));
    }

    @Test
    void shouldRequireCode() {
        assertThrows(RequiredFieldException.class, () -> CouponCode.sanitize(null));
    }
}
