package br.com.coupon.api.web.controller;

import br.com.coupon.api.application.usecase.createcoupon.CreateCouponUseCase;
import br.com.coupon.api.application.usecase.deletecoupon.DeleteCouponUseCase;
import br.com.coupon.api.application.usecase.findcoupon.FindCouponUseCase;
import br.com.coupon.api.domain.model.Coupon;
import br.com.coupon.api.web.dto.CouponResponse;
import br.com.coupon.api.web.dto.CreateCouponRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    private final CreateCouponUseCase createCouponUseCase;
    private final FindCouponUseCase findCouponUseCase;
    private final DeleteCouponUseCase deleteCouponUseCase;

    public CouponController(CreateCouponUseCase createCouponUseCase, FindCouponUseCase findCouponUseCase, DeleteCouponUseCase deleteCouponUseCase) {
        this.createCouponUseCase = createCouponUseCase;
        this.findCouponUseCase = findCouponUseCase;
        this.deleteCouponUseCase = deleteCouponUseCase;
    }

    @PostMapping
    public ResponseEntity<CouponResponse> createCoupon(@Valid @RequestBody CreateCouponRequest request) {
        Coupon coupon = createCouponUseCase.execute(request.toInput());
        return ResponseEntity.created(URI.create("/coupon/" + coupon.getId()))
                .body(CouponResponse.from(coupon));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> findCouponById(@PathVariable UUID id) {
        Coupon coupon = findCouponUseCase.execute(id);
        return ResponseEntity.ok(CouponResponse.from(coupon));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable UUID id) {
        deleteCouponUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
