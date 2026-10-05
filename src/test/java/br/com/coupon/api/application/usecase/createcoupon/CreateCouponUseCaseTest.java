package br.com.coupon.api.application.usecase.createcoupon;

import br.com.coupon.api.application.port.CouponRepository;
import br.com.coupon.api.domain.exception.InvalidDiscountValueException;
import br.com.coupon.api.domain.model.Coupon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCouponUseCaseTest {

    private static final String CODE = "ABC123";
    private static final String DESCRIPTION = "Cupom de teste";
    private static final BigDecimal DISCOUNT_VALUE = new BigDecimal("10");
    private static final LocalDateTime EXPIRATION_DATE = LocalDateTime.now().plusDays(1);
    private static final boolean PUBLISHED = false;
    private static final BigDecimal INVALID_DISCOUNT_VALUE = new BigDecimal("0.3");

    @Mock
    private CouponRepository repository;

    @InjectMocks
    private CreateCouponUseCase useCase;

    @Test
    void shouldCreateAndSaveCoupon() {
        CreateCouponInput input = newInput(DISCOUNT_VALUE);
        Coupon saved = newCoupon();
        when(repository.save(any(Coupon.class))).thenReturn(saved);

        Coupon result = useCase.execute(input);

        verify(repository).save(any(Coupon.class));
        assertSame(saved, result);
    }

    @Test
    void shouldNotSaveWhenCouponIsInvalid() {
        CreateCouponInput input = newInput(INVALID_DISCOUNT_VALUE);

        assertThrows(InvalidDiscountValueException.class, () -> useCase.execute(input));

        verify(repository, never()).save(any());
    }

    private static CreateCouponInput newInput(BigDecimal discountValue) {
        return new CreateCouponInput(
                CODE,
                DESCRIPTION,
                discountValue,
                EXPIRATION_DATE,
                PUBLISHED
        );
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
