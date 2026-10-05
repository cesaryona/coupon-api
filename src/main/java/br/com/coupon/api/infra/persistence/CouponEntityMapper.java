package br.com.coupon.api.infra.persistence;

import br.com.coupon.api.domain.model.Coupon;

final class CouponEntityMapper {

    private CouponEntityMapper() {
    }

    static CouponEntity toEntity(Coupon coupon) {
        CouponEntity entity = new CouponEntity();
        entity.setId(coupon.getId());
        entity.setCode(coupon.getCode().value());
        entity.setDescription(coupon.getDescription());
        entity.setDiscountValue(coupon.getDiscountValue().value());
        entity.setExpirationDate(coupon.getExpirationDate());
        entity.setStatus(coupon.getStatus());
        entity.setPublished(coupon.isPublished());
        entity.setRedeemed(coupon.isRedeemed());
        entity.setDeletedAt(coupon.getDeletedAt());

        return entity;
    }

    static Coupon toDomain(CouponEntity entity) {
        return Coupon.restore(
                entity.getId(),
                entity.getCode(),
                entity.getDescription(),
                entity.getDiscountValue(),
                entity.getExpirationDate(),
                entity.getStatus(),
                entity.isPublished(),
                entity.isRedeemed(),
                entity.getDeletedAt()
        );
    }
}
