package br.com.coupon.api.application.usecase.findcoupon;

import br.com.coupon.api.application.port.CouponRepository;
import br.com.coupon.api.domain.exception.CouponNotFoundException;
import br.com.coupon.api.domain.model.Coupon;
import br.com.coupon.api.domain.model.CouponStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindCouponUseCaseTest {

    private static final String CODE = "ABC123";
    private static final String DESCRIPTION = "Cupom de teste";
    private static final BigDecimal DISCOUNT_VALUE = new BigDecimal("10");
    private static final LocalDateTime EXPIRATION_DATE = LocalDateTime.now().plusDays(1);
    private static final boolean PUBLISHED = false;

    @Mock
    private CouponRepository repository;

    @InjectMocks
    private FindCouponUseCase useCase;

    @Test
    void shouldReturnCouponWhenExists() {
        Coupon coupon = newCoupon();
        when(repository.findById(coupon.getId())).thenReturn(Optional.of(coupon));

        Coupon found = useCase.execute(coupon.getId());

        assertEquals(coupon, found);
    }

    @Test
    void shouldReturnDeletedCouponWithDeletedStatus() {
        Coupon coupon = newCoupon();
        coupon.delete();
        when(repository.findById(coupon.getId())).thenReturn(Optional.of(coupon));

        Coupon found = useCase.execute(coupon.getId());

        assertEquals(CouponStatus.DELETED, found.getStatus());
    }

    @Test
    void shouldThrowNotFoundWhenCouponDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CouponNotFoundException.class, () -> useCase.execute(id));
    }

    private static Coupon newCoupon() {
        return Coupon.create(
                CODE,
                DESCRIPTION,
                DISCOUNT_VALUE,
                EXPIRATION_DATE,
                PUBLISHED
        );
    }
}
