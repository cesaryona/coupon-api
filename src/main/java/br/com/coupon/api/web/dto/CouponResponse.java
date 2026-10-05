package br.com.coupon.api.web.dto;

import br.com.coupon.api.domain.model.Coupon;
import br.com.coupon.api.domain.model.CouponStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CouponResponse(UUID id,
                             String code,
                             String description,
                             BigDecimal discountValue,
                             LocalDateTime expirationDate,
                             CouponStatus status,
                             boolean published,
                             boolean redeemed) {

    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getCode().value(),
                coupon.getDescription(),
                coupon.getDiscountValue().value(),
                coupon.getExpirationDate(),
                coupon.getStatus(),
                coupon.isPublished(),
                coupon.isRedeemed()
        );
    }
}
