package br.com.coupon.api.domain.exception;

import java.math.BigDecimal;

public class InvalidDiscountValueException extends DomainException {

    public InvalidDiscountValueException(BigDecimal value) {
        super("Invalid discount value: " + value + ". Minimum is 0.5");
    }
}
