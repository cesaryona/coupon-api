package br.com.coupon.api.application.usecase.findcoupon;

import br.com.coupon.api.application.port.CouponRepository;
import br.com.coupon.api.domain.exception.CouponNotFoundException;
import br.com.coupon.api.domain.model.Coupon;

import java.util.UUID;

public class FindCouponUseCase {

    private final CouponRepository repository;

    public FindCouponUseCase(CouponRepository repository) {
        this.repository = repository;
    }

    public Coupon execute(UUID id) {
        return repository.findById(id).orElseThrow(() -> new CouponNotFoundException(id));
    }
}
