ALTER TABLE clients
    DROP CONSTRAINT IF EXISTS uq_clients_phone;

ALTER TABLE clients
    ADD COLUMN IF NOT EXISTS external_id TEXT,
    ADD COLUMN IF NOT EXISTS password_hash TEXT,
    ADD COLUMN IF NOT EXISTS role TEXT NOT NULL DEFAULT 'client';

CREATE UNIQUE INDEX IF NOT EXISTS ux_clients_external_id
    ON clients (external_id)
    WHERE external_id IS NOT NULL;

ALTER TABLE clients
    DROP CONSTRAINT IF EXISTS chk_clients_role;

ALTER TABLE clients
    ADD CONSTRAINT chk_clients_role CHECK (role IN ('client', 'admin'));

ALTER TABLE categories
    ADD COLUMN IF NOT EXISTS external_id TEXT,
    ADD COLUMN IF NOT EXISTS slug TEXT,
    ADD COLUMN IF NOT EXISTS title_ua TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS title_de TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS title_gb TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS description_ua TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS description_de TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS description_gb TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS sort_order INTEGER NOT NULL DEFAULT 0;

CREATE UNIQUE INDEX IF NOT EXISTS ux_categories_external_id
    ON categories (external_id)
    WHERE external_id IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_categories_slug
    ON categories (slug)
    WHERE slug IS NOT NULL;

ALTER TABLE services
    ADD COLUMN IF NOT EXISTS external_id TEXT,
    ADD COLUMN IF NOT EXISTS title_ua TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS title_de TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS title_gb TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS sort_order INTEGER NOT NULL DEFAULT 0;

CREATE UNIQUE INDEX IF NOT EXISTS ux_services_external_id
    ON services (external_id)
    WHERE external_id IS NOT NULL;

ALTER TABLE masters
    ADD COLUMN IF NOT EXISTS external_id TEXT,
    ADD COLUMN IF NOT EXISTS display_name TEXT,
    ADD COLUMN IF NOT EXISTS role_ua TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS role_de TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS role_gb TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS initials TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS experience_label TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS sort_order INTEGER NOT NULL DEFAULT 0;

CREATE UNIQUE INDEX IF NOT EXISTS ux_masters_external_id
    ON masters (external_id)
    WHERE external_id IS NOT NULL;

CREATE TABLE IF NOT EXISTS master_specialty_categories
(
    master_id   BIGINT NOT NULL REFERENCES masters (id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES categories (id) ON DELETE CASCADE,
    PRIMARY KEY (master_id, category_id)
);

ALTER TABLE products
    ADD COLUMN IF NOT EXISTS external_id TEXT,
    ADD COLUMN IF NOT EXISTS title_ua TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS title_de TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS title_gb TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS icon TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS sort_order INTEGER NOT NULL DEFAULT 0;

CREATE UNIQUE INDEX IF NOT EXISTS ux_products_external_id
    ON products (external_id)
    WHERE external_id IS NOT NULL;

ALTER TABLE drinks
    ADD COLUMN IF NOT EXISTS external_id TEXT,
    ADD COLUMN IF NOT EXISTS title_ua TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS title_de TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS title_gb TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS icon TEXT NOT NULL DEFAULT '',
    ADD COLUMN IF NOT EXISTS sort_order INTEGER NOT NULL DEFAULT 0;

CREATE UNIQUE INDEX IF NOT EXISTS ux_drinks_external_id
    ON drinks (external_id)
    WHERE external_id IS NOT NULL;

CREATE TABLE IF NOT EXISTS booking_slots
(
    id          BIGSERIAL PRIMARY KEY,
    external_id TEXT    NOT NULL UNIQUE,
    period      TEXT    NOT NULL,
    time        TIME    NOT NULL,
    sort_order  INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT chk_booking_slots_period CHECK (period IN ('morning', 'afternoon', 'evening'))
);

CREATE TABLE IF NOT EXISTS trends
(
    id             BIGSERIAL PRIMARY KEY,
    external_id    TEXT    NOT NULL UNIQUE,
    title_ua       TEXT    NOT NULL DEFAULT '',
    title_de       TEXT    NOT NULL DEFAULT '',
    title_gb       TEXT    NOT NULL DEFAULT '',
    description_ua TEXT    NOT NULL DEFAULT '',
    description_de TEXT    NOT NULL DEFAULT '',
    description_gb TEXT    NOT NULL DEFAULT '',
    gradient       TEXT    NOT NULL,
    emoji          TEXT    NOT NULL,
    sort_order     INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS salon_bookings
(
    id                  BIGSERIAL PRIMARY KEY,
    external_id         TEXT        NOT NULL UNIQUE,
    client_id           BIGINT REFERENCES clients (id) ON DELETE SET NULL,
    category_id         BIGINT REFERENCES categories (id) ON DELETE SET NULL,
    service_id          BIGINT REFERENCES services (id) ON DELETE SET NULL,
    master_id           BIGINT REFERENCES masters (id) ON DELETE SET NULL,
    customer_first_name TEXT        NOT NULL,
    customer_last_name  TEXT        NOT NULL,
    customer_phone      TEXT        NOT NULL,
    customer_email      TEXT,
    booking_date        DATE        NOT NULL,
    booking_time        TIME        NOT NULL,
    status              TEXT        NOT NULL DEFAULT 'new',
    note                TEXT        NOT NULL DEFAULT '',
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    sort_order          INTEGER     NOT NULL DEFAULT 0,

    CONSTRAINT chk_salon_bookings_status CHECK (status IN ('new', 'confirmed', 'completed', 'cancelled'))
);
