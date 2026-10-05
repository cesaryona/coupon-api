package br.com.coupon.api.infra.config;

import br.com.coupon.api.application.port.CouponRepository;
import br.com.coupon.api.application.usecase.createcoupon.CreateCouponUseCase;
import br.com.coupon.api.application.usecase.deletecoupon.DeleteCouponUseCase;
import br.com.coupon.api.application.usecase.findcoupon.FindCouponUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public CreateCouponUseCase createCouponUseCase(CouponRepository couponRepository) {
        return new CreateCouponUseCase(couponRepository);
    }

    @Bean
    public FindCouponUseCase findCouponUseCase(CouponRepository couponRepository) {
        return new FindCouponUseCase(couponRepository);
    }

    @Bean
    public DeleteCouponUseCase deleteCouponUseCase(CouponRepository couponRepository) {
        return new DeleteCouponUseCase(couponRepository);
    }
}
