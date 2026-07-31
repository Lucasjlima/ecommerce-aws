CREATE TABLE payments (
    id               UUID           DEFAULT gen_random_uuid(),
    order_id         UUID           NOT NULL,
    payment_provider VARCHAR(20)   NOT NULL,
    payment_status   VARCHAR(20)   NOT NULL,
    transaction_id   VARCHAR(255)   NOT NULL,
    amount           NUMERIC(15, 2) NOT NULL,

    CONSTRAINT pk_payments                PRIMARY KEY (id),
    CONSTRAINT fk_payments_order          FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT uq_payments_order          UNIQUE (order_id),
    CONSTRAINT uq_payments_transaction_id UNIQUE (transaction_id)
);
