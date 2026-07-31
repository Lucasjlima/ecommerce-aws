CREATE TABLE categories (
    id   UUID         DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL,

    CONSTRAINT pk_categories      PRIMARY KEY (id),
    CONSTRAINT uq_categories_slug UNIQUE (slug)
);

CREATE TABLE products (
    id             UUID                     DEFAULT gen_random_uuid(),
    name           VARCHAR(255)             NOT NULL,
    description    VARCHAR(1000)            NOT NULL,
    price          NUMERIC(15, 2)           NOT NULL,
    stock_quantity BIGINT                   NOT NULL,
    category_id    UUID                     NOT NULL,
    img_key        VARCHAR(500),
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT pk_products          PRIMARY KEY (id),
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories (id)
);
