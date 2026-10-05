package br.com.coupon.api.application.usecase.createcoupon;

import br.com.coupon.api.application.port.CouponRepository;
import br.com.coupon.api.domain.model.Coupon;

public class CreateCouponUseCase {

    private final CouponRepository couponRepository;

    public CreateCouponUseCase(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public Coupon execute(CreateCouponInput input) {
        Coupon coupon = Coupon.create(
                input.code(),
                input.description(),
                input.discountValue(),
                input.expirationDate(),
                input.published()
        );
        return couponRepository.save(coupon);
    }
}
