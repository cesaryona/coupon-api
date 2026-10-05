package br.com.coupon.api.domain.exception;

public class InvalidCouponCodeException extends DomainException {

    public InvalidCouponCodeException() {
        super("Invalid coupon code. It must have exactly 6 alphanumeric characters, ignoring special characters");
    }
}
