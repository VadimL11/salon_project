ALTER TABLE salon_bookings
    ADD COLUMN IF NOT EXISTS guest_external_id TEXT;

CREATE INDEX IF NOT EXISTS ix_salon_bookings_guest_external_id
    ON salon_bookings (guest_external_id)
    WHERE guest_external_id IS NOT NULL;

ALTER TABLE frontend_care_orders
    ADD COLUMN IF NOT EXISTS guest_external_id TEXT;

CREATE INDEX IF NOT EXISTS ix_frontend_care_orders_guest_external_id
    ON frontend_care_orders (guest_external_id)
    WHERE guest_external_id IS NOT NULL;

ALTER TABLE frontend_drink_orders
    ADD COLUMN IF NOT EXISTS guest_external_id TEXT;

CREATE INDEX IF NOT EXISTS ix_frontend_drink_orders_guest_external_id
    ON frontend_drink_orders (guest_external_id)
    WHERE guest_external_id IS NOT NULL;
