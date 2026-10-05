package br.com.coupon.api.domain.model;

import br.com.coupon.api.domain.exception.InvalidDiscountValueException;
import br.com.coupon.api.domain.exception.RequiredFieldException;

import java.math.BigDecimal;

public record DiscountValue(BigDecimal value) {

    public static final BigDecimal MINIMUM = new BigDecimal("0.5");

    public DiscountValue {
        if (value == null) {
            throw new RequiredFieldException("discountValue");
        }
        if (value.compareTo(MINIMUM) < 0) {
            throw new InvalidDiscountValueException(value);
        }
    }
}
