CREATE TABLE clients
(
    id         BIGSERIAL PRIMARY KEY,
    first_name TEXT        NOT NULL,
    last_name  TEXT        NOT NULL,
    phone      TEXT,
    email      TEXT,
    language   TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_clients_email UNIQUE (email),
    CONSTRAINT uq_clients_phone UNIQUE (phone)
);

CREATE TABLE categories
(
    id          BIGSERIAL PRIMARY KEY,
    name        TEXT NOT NULL,
    icon        TEXT,
    description TEXT
);

CREATE TABLE services
(
    id          BIGSERIAL PRIMARY KEY,
    category_id BIGINT         NOT NULL REFERENCES categories (id),
    name        TEXT           NOT NULL,
    duration    INTEGER        NOT NULL, -- minutes
    price       NUMERIC(10, 2) NOT NULL DEFAULT 0,
    active      BOOLEAN        NOT NULL DEFAULT TRUE,

    CONSTRAINT chk_services_duration CHECK (duration > 0),
    CONSTRAINT chk_services_price CHECK (price >= 0)
);

CREATE TABLE masters
(
    id         BIGSERIAL PRIMARY KEY,
    first_name TEXT          NOT NULL,
    last_name  TEXT          NOT NULL,
    photo      TEXT,
    rating     NUMERIC(3, 2) NOT NULL DEFAULT 0,
    active     BOOLEAN       NOT NULL DEFAULT TRUE,

    CONSTRAINT chk_masters_rating CHECK (rating BETWEEN 0 AND 5)
);

CREATE TABLE schedules
(
    id         BIGSERIAL PRIMARY KEY,
    master_id  BIGINT  NOT NULL REFERENCES masters (id) ON DELETE CASCADE,
    work_date  DATE    NOT NULL,
    start_time TIME    NOT NULL,
    end_time   TIME    NOT NULL,
    available  BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT chk_schedule_time CHECK (end_time > start_time)
);

-- для коректного FK з appointments
ALTER TABLE schedules
    ADD CONSTRAINT uq_schedule_master UNIQUE (id, master_id);

CREATE TABLE appointments
(
    id          BIGSERIAL PRIMARY KEY,
    client_id   BIGINT         NOT NULL REFERENCES clients (id),
    master_id   BIGINT         NOT NULL REFERENCES masters (id),
    service_id  BIGINT         NOT NULL REFERENCES services (id),
    schedule_id BIGINT         NOT NULL,
    status      TEXT           NOT NULL DEFAULT 'created',
    price       NUMERIC(10, 2) NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_schedule_master
        FOREIGN KEY (schedule_id, master_id)
            REFERENCES schedules (id, master_id),

    CONSTRAINT chk_appointment_price CHECK (price >= 0),
    CONSTRAINT chk_appointment_status CHECK (
        status IN ('created', 'confirmed', 'completed', 'cancelled', 'no_show')
        )
);

CREATE TABLE reviews
(
    id         BIGSERIAL PRIMARY KEY,
    client_id  BIGINT      NOT NULL REFERENCES clients (id) ON DELETE CASCADE,
    master_id  BIGINT      NOT NULL REFERENCES masters (id) ON DELETE CASCADE,
    rating     SMALLINT    NOT NULL,
    comment    TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_review_rating CHECK (rating BETWEEN 1 AND 5)
);

CREATE TABLE products
(
    id    BIGSERIAL PRIMARY KEY,
    name  TEXT           NOT NULL,
    brand TEXT,
    price NUMERIC(10, 2) NOT NULL DEFAULT 0,
    stock INTEGER        NOT NULL DEFAULT 0,

    CONSTRAINT chk_products_price CHECK (price >= 0),
    CONSTRAINT chk_products_stock CHECK (stock >= 0)
);

CREATE TABLE product_orders
(
    id             BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT         NOT NULL REFERENCES appointments (id) ON DELETE CASCADE,
    product_id     BIGINT         NOT NULL REFERENCES products (id),
    quantity       INTEGER        NOT NULL DEFAULT 1,
    price          NUMERIC(10, 2) NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ    NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_product_orders_qty CHECK (quantity > 0),
    CONSTRAINT chk_product_orders_price CHECK (price >= 0)
);

CREATE TABLE drinks
(
    id        BIGSERIAL PRIMARY KEY,
    name      TEXT           NOT NULL,
    price     NUMERIC(10, 2) NOT NULL DEFAULT 0,
    available BOOLEAN        NOT NULL DEFAULT TRUE,

    CONSTRAINT chk_drinks_price CHECK (price >= 0)
);

CREATE TABLE drink_orders
(
    id             BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT         NOT NULL REFERENCES appointments (id) ON DELETE CASCADE,
    drink_id       BIGINT         NOT NULL REFERENCES drinks (id),
    quantity       INTEGER        NOT NULL DEFAULT 1,
    price          NUMERIC(10, 2) NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ    NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_drink_orders_qty CHECK (quantity > 0),
    CONSTRAINT chk_drink_orders_price CHECK (price >= 0)
);