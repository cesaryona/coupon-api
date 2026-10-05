package br.com.coupon.api.domain.exception;

import java.util.UUID;

public class CouponNotFoundException extends DomainException {

    public CouponNotFoundException(UUID id) {
        super("Coupon not found: " + id);
    }
}
