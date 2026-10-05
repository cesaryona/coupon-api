package br.com.coupon.api.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CouponJpaRepository extends JpaRepository<CouponEntity, UUID> {
}
