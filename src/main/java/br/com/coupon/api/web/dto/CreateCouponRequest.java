package br.com.coupon.api.web.dto;

import br.com.coupon.api.application.usecase.createcoupon.CreateCouponInput;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateCouponRequest(
        @NotBlank String code,
        @NotBlank @Size(max = 500) String description,
        @NotNull @Digits(integer = 8, fraction = 2) BigDecimal discountValue,
        @NotNull LocalDateTime expirationDate,
        Boolean published) {

    public CreateCouponInput toInput() {
        return new CreateCouponInput(
                code,
                description,
                discountValue,
                expirationDate,
                Boolean.TRUE.equals(published)
        );
    }
}
