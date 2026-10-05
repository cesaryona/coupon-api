package br.com.coupon.api.domain.model;

import br.com.coupon.api.domain.exception.CouponAlreadyDeletedException;
import br.com.coupon.api.domain.exception.ExpirationDateInPastException;
import br.com.coupon.api.domain.exception.RequiredFieldException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Coupon {

    private final UUID id;
    private final CouponCode code;
    private final String description;
    private final DiscountValue discountValue;
    private final LocalDateTime expirationDate;
    private final boolean published;
    private final boolean redeemed;
    private CouponStatus status;
    private LocalDateTime deletedAt;

    private Coupon(UUID id,
                   CouponCode code,
                   String description,
                   DiscountValue discountValue,
                   LocalDateTime expirationDate,
                   CouponStatus status,
                   boolean published,
                   boolean redeemed,
                   LocalDateTime deletedAt) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.status = status;
        this.published = published;
        this.redeemed = redeemed;
        this.deletedAt = deletedAt;
    }

    public static Coupon create(String code,
                                String description,
                                BigDecimal discountValue,
                                LocalDateTime expirationDate,
                                boolean published) {
        CouponCode couponCode = CouponCode.sanitize(code);
        requireText(description, "description");
        DiscountValue discount = new DiscountValue(discountValue);
        validateExpirationDate(expirationDate);

        return new Coupon(
                UUID.randomUUID(),
                couponCode,
                description.trim(),
                discount,
                expirationDate,
                CouponStatus.ACTIVE,
                published,
                false,
                null
        );
    }


    public static Coupon restore(UUID id,
                                 String code,
                                 String description,
                                 BigDecimal discountValue,
                                 LocalDateTime expirationDate,
                                 CouponStatus status,
                                 boolean published,
                                 boolean redeemed,
                                 LocalDateTime deletedAt) {
        return new Coupon(
                Objects.requireNonNull(id, "id"),
                new CouponCode(code),
                Objects.requireNonNull(description, "description"),
                new DiscountValue(discountValue),
                Objects.requireNonNull(expirationDate, "expirationDate"),
                Objects.requireNonNull(status, "status"),
                published,
                redeemed,
                deletedAt
        );
    }

    public void delete() {
        if (isDeleted()) {
            throw new CouponAlreadyDeletedException(id);
        }
        this.status = CouponStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return status == CouponStatus.DELETED;
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new RequiredFieldException(field);
        }
    }

    private static void validateExpirationDate(LocalDateTime expirationDate) {
        if (expirationDate == null) {
            throw new RequiredFieldException("expirationDate");
        }
        if (expirationDate.isBefore(LocalDateTime.now())) {
            throw new ExpirationDateInPastException(expirationDate);
        }
    }

    public UUID getId() {
        return id;
    }

    public CouponCode getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public DiscountValue getDiscountValue() {
        return discountValue;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public CouponStatus getStatus() {
        return status;
    }

    public boolean isPublished() {
        return published;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Coupon other && id.equals(other.id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
