package br.com.coupon.api.application.usecase.createcoupon;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateCouponInput(String code,
                                  String description,
                                  BigDecimal discountValue,
                                  LocalDateTime expirationDate,
                                  boolean published) {
}
