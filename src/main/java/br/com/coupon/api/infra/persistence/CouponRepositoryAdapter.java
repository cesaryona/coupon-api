package br.com.coupon.api.infra.persistence;

import br.com.coupon.api.application.port.CouponRepository;
import br.com.coupon.api.domain.model.Coupon;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CouponRepositoryAdapter implements CouponRepository {

    private final CouponJpaRepository jpaRepository;

    public CouponRepositoryAdapter(CouponJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Coupon save(Coupon coupon) {
        CouponEntity entity = jpaRepository.save(CouponEntityMapper.toEntity(coupon));
        return CouponEntityMapper.toDomain(entity);
    }

    @Override
    public Optional<Coupon> findById(UUID id) {
        return jpaRepository.findById(id).map(CouponEntityMapper::toDomain);
    }
}
