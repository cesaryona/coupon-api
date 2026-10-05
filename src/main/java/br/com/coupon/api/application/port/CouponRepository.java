package br.com.coupon.api.application.port;

import br.com.coupon.api.domain.model.Coupon;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepository {

    Coupon save(Coupon coupon);

    Optional<Coupon> findById(UUID id);
}
