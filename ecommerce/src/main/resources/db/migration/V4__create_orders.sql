CREATE TABLE orders (
    id           UUID                     DEFAULT gen_random_uuid(),
    user_id      UUID                     NOT NULL,
    order_status VARCHAR(20)          NOT NULL,
    total_amount NUMERIC(15, 2)           NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT pk_orders      PRIMARY KEY (id),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE order_items (
    id                UUID           DEFAULT gen_random_uuid(),
    order_id          UUID           NOT NULL,
    product_id        UUID           NOT NULL,
    quantity          BIGINT         NOT NULL,
    price_at_purchase NUMERIC(15, 2) NOT NULL,

    CONSTRAINT pk_order_items         PRIMARY KEY (id),
    CONSTRAINT fk_order_items_order   FOREIGN KEY (order_id)   REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products (id)
);
