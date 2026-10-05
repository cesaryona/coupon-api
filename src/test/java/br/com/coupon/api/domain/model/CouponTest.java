package br.com.coupon.api.domain.model;

import br.com.coupon.api.domain.exception.CouponAlreadyDeletedException;
import br.com.coupon.api.domain.exception.ExpirationDateInPastException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class CouponTest {

    private static final String CODE = "ABC123";
    private static final String DESCRIPTION = "Cupom de teste";
    private static final BigDecimal DISCOUNT_VALUE = new BigDecimal("10");
    private static final LocalDateTime EXPIRATION_DATE = LocalDateTime.now().plusDays(1);
    private static final boolean PUBLISHED = false;

    @Nested
    class Create {

        @Test
        void shouldCreateActiveCoupon() {
            Coupon coupon = newCoupon();

            assertNotNull(coupon.getId());
            assertEquals(CODE, coupon.getCode().value());
            assertEquals(DESCRIPTION, coupon.getDescription());
            assertEquals(DISCOUNT_VALUE, coupon.getDiscountValue().value());
            assertEquals(EXPIRATION_DATE, coupon.getExpirationDate());
            assertEquals(CouponStatus.ACTIVE, coupon.getStatus());
            assertFalse(coupon.isPublished());
            assertNull(coupon.getDeletedAt());
        }

        @Test
        void shouldRejectExpirationDateInPast() {
            LocalDateTime yesterday = LocalDateTime.now().minusDays(1);

            assertThrows(ExpirationDateInPastException.class,
                    () -> Coupon.create(CODE, DESCRIPTION, DISCOUNT_VALUE, yesterday, PUBLISHED));
        }

    }

    @Nested
    class Delete {

        @Test
        void shouldSoftDeleteCoupon() {
            Coupon coupon = newCoupon();

            coupon.delete();

            assertEquals(CouponStatus.DELETED, coupon.getStatus());
            assertNotNull(coupon.getDeletedAt());
            assertEquals(CODE, coupon.getCode().value());
        }

        @Test
        void shouldNotDeleteTwice() {
            Coupon coupon = newCoupon();
            coupon.delete();

            assertThrows(CouponAlreadyDeletedException.class, coupon::delete);
        }

        @Test
        void shouldDeleteExpiredCoupon() {
            Coupon coupon = Coupon.restore(
                    UUID.randomUUID(),
                    CODE,
                    DESCRIPTION,
                    DISCOUNT_VALUE,
                    LocalDateTime.now().minusDays(1),
                    CouponStatus.ACTIVE,
                    PUBLISHED,
                    false,
                    null
            );

            coupon.delete();

            assertEquals(CouponStatus.DELETED, coupon.getStatus());
        }

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
