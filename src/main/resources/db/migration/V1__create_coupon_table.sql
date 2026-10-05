CREATE TABLE coupon (
    id              UUID           NOT NULL,
    code            VARCHAR(6)     NOT NULL,
    description     VARCHAR(500)   NOT NULL,
    discount_value  NUMERIC(10, 2) NOT NULL,
    expiration_date TIMESTAMP      NOT NULL,
    status          VARCHAR(20)    NOT NULL,
    published       BOOLEAN        NOT NULL,
    redeemed        BOOLEAN        NOT NULL,
    deleted_at      TIMESTAMP,
    CONSTRAINT pk_coupon PRIMARY KEY (id)
);
