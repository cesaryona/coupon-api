package br.com.coupon.api.domain.model;

import br.com.coupon.api.domain.exception.InvalidCouponCodeException;
import br.com.coupon.api.domain.exception.RequiredFieldException;

import java.util.regex.Pattern;

public record CouponCode(String value) {

    public static final int LENGTH = 6;

    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^A-Za-z0-9]");
    private static final Pattern VALID = Pattern.compile("[A-Za-z0-9]{" + LENGTH + "}");

    public CouponCode {
        if (value == null || !VALID.matcher(value).matches()) {
            throw new InvalidCouponCodeException();
        }
    }

    public static CouponCode sanitize(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new RequiredFieldException("code");
        }
        return new CouponCode(NON_ALPHANUMERIC.matcher(raw).replaceAll(""));
    }

}
