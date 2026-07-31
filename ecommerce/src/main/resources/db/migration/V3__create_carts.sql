CREATE TABLE carts (
    id          UUID                     DEFAULT gen_random_uuid(),
    user_id     UUID                     NOT NULL,
    cart_status VARCHAR(20),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT pk_carts      PRIMARY KEY (id),
    CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE cart_items (
    id         UUID   DEFAULT gen_random_uuid(),
    cart_id    UUID   NOT NULL,
    product_id UUID   NOT NULL,
    quantity   BIGINT NOT NULL,

    CONSTRAINT pk_cart_items              PRIMARY KEY (id),
    CONSTRAINT fk_cart_items_cart         FOREIGN KEY (cart_id)    REFERENCES carts (id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product      FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT uq_cart_items_cart_product UNIQUE (cart_id, product_id)
);
