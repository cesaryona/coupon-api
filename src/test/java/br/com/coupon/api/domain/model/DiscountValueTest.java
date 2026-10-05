package br.com.coupon.api.domain.model;

import br.com.coupon.api.domain.exception.InvalidDiscountValueException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DiscountValueTest {

    @Test
    void shouldAcceptMinimumValue() {
        DiscountValue discount = new DiscountValue(new BigDecimal("0.5"));

        assertEquals(new BigDecimal("0.5"), discount.value());
    }

    @Test
    void shouldRejectValueBelowMinimum() {
        assertThrows(InvalidDiscountValueException.class, () -> new DiscountValue(new BigDecimal("0.49")));
    }
}
