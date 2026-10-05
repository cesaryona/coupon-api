package br.com.coupon.api.domain.exception;

public class RequiredFieldException extends DomainException {

    public RequiredFieldException(String field) {
        super(field + " is required");
    }
}
