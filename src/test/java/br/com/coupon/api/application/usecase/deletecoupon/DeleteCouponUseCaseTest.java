package br.com.coupon.api.application.usecase.deletecoupon;

import br.com.coupon.api.application.port.CouponRepository;
import br.com.coupon.api.domain.exception.CouponAlreadyDeletedException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteCouponUseCaseTest {

    private static final String CODE = "ABC123";
    private static final String DESCRIPTION = "Cupom de teste";
    private static final BigDecimal DISCOUNT_VALUE = new BigDecimal("10");
    private static final LocalDateTime EXPIRATION_DATE = LocalDateTime.now().plusDays(1);
    private static final boolean PUBLISHED = false;

    @Mock
    private CouponRepository repository;

    @InjectMocks
    private DeleteCouponUseCase useCase;

    @Test
    void shouldDeleteCouponWhenExists() {
        Coupon coupon = newCoupon();
        when(repository.findById(coupon.getId())).thenReturn(Optional.of(coupon));

        useCase.execute(coupon.getId());

        verify(repository).save(coupon);

        assertEquals(CouponStatus.DELETED, coupon.getStatus());
        assertNotNull(coupon.getDeletedAt());
    }

    @Test
    void shouldThrowNotFoundWhenCouponDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CouponNotFoundException.class, () -> useCase.execute(id));

        verify(repository).findById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldNotDeleteCouponTwice() {
        Coupon coupon = newCoupon();
        coupon.delete();
        when(repository.findById(coupon.getId())).thenReturn(Optional.of(coupon));

        assertThrows(CouponAlreadyDeletedException.class, () -> useCase.execute(coupon.getId()));

        verify(repository).findById(coupon.getId());
        verifyNoMoreInteractions(repository);
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
