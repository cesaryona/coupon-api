package br.com.coupon.api.domain.exception;

import java.time.LocalDateTime;

public class ExpirationDateInPastException extends DomainException {

    public ExpirationDateInPastException(LocalDateTime expirationDate) {
        super("Expiration date cannot be in the past: " + expirationDate);
    }
}
