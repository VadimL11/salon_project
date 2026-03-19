ALTER TABLE trends
    ADD COLUMN IF NOT EXISTS image_url TEXT;

CREATE TABLE IF NOT EXISTS master_credentials
(
    id              BIGSERIAL PRIMARY KEY,
    external_id     TEXT    NOT NULL UNIQUE,
    master_id       BIGINT  NOT NULL REFERENCES masters (id) ON DELETE CASCADE,
    credential_name TEXT    NOT NULL,
    credential_type TEXT    NOT NULL,
    file_url        TEXT    NOT NULL,
    sort_order      INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS ix_master_credentials_master_id
    ON master_credentials (master_id, sort_order);

CREATE TABLE IF NOT EXISTS frontend_care_orders
(
    id             BIGSERIAL PRIMARY KEY,
    external_id    TEXT            NOT NULL UNIQUE,
    client_id      BIGINT REFERENCES clients (id) ON DELETE SET NULL,
    customer_email TEXT,
    total_amount   NUMERIC(12, 2)  NOT NULL DEFAULT 0,
    created_at     TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS ix_frontend_care_orders_created_at
    ON frontend_care_orders (created_at DESC);

CREATE INDEX IF NOT EXISTS ix_frontend_care_orders_client_id
    ON frontend_care_orders (client_id);

CREATE TABLE IF NOT EXISTS frontend_care_order_items
(
    id                  BIGSERIAL PRIMARY KEY,
    care_order_id       BIGINT          NOT NULL REFERENCES frontend_care_orders (id) ON DELETE CASCADE,
    product_external_id TEXT            NOT NULL,
    item_name           TEXT            NOT NULL,
    unit_price          NUMERIC(12, 2)  NOT NULL DEFAULT 0,
    quantity            INTEGER         NOT NULL,
    sort_order          INTEGER         NOT NULL DEFAULT 0,

    CONSTRAINT chk_frontend_care_order_items_quantity CHECK (quantity > 0)
);

CREATE INDEX IF NOT EXISTS ix_frontend_care_order_items_order_id
    ON frontend_care_order_items (care_order_id, sort_order);

CREATE TABLE IF NOT EXISTS frontend_drink_orders
(
    id                BIGSERIAL PRIMARY KEY,
    external_id       TEXT        NOT NULL UNIQUE,
    client_id         BIGINT REFERENCES clients (id) ON DELETE SET NULL,
    customer_email    TEXT,
    drink_external_id TEXT        NOT NULL,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS ix_frontend_drink_orders_created_at
    ON frontend_drink_orders (created_at DESC);

CREATE INDEX IF NOT EXISTS ix_frontend_drink_orders_client_id
    ON frontend_drink_orders (client_id);
