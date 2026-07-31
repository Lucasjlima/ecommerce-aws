CREATE TABLE roles (
    id   UUID        DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL,

    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE users (
    id            UUID                     DEFAULT gen_random_uuid(),
    user_name     VARCHAR(255)             NOT NULL,
    user_email    VARCHAR(255)             NOT NULL,
    user_password VARCHAR(255)             NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT pk_users       PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (user_email)
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,

    CONSTRAINT pk_user_roles      PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);
