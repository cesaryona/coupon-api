package br.com.coupon.api.domain.exception;

import java.util.UUID;

public class CouponAlreadyDeletedException extends DomainException {

    public CouponAlreadyDeletedException(UUID id) {
        super("Coupon already deleted: " + id);
    }
}
