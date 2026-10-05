package br.com.coupon.api.application.usecase.deletecoupon;

import br.com.coupon.api.application.port.CouponRepository;
import br.com.coupon.api.domain.exception.CouponNotFoundException;
import br.com.coupon.api.domain.model.Coupon;

import java.util.UUID;

public class DeleteCouponUseCase {

    private final CouponRepository repository;

    public DeleteCouponUseCase(CouponRepository repository) {
        this.repository = repository;
    }

    public void execute(UUID id) {
        Coupon coupon = repository.findById(id).orElseThrow(() -> new CouponNotFoundException(id));
        coupon.delete();
        repository.save(coupon);
    }
}
