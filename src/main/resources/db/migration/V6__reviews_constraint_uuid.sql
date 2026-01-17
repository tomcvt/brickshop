-- src/main/resources/db/migration/V6__reviews_constraint_uuid.sql

-- Migration V6: Add Reviews Table Constraint and UUID

ALTER TABLE IF EXISTS reviews
ADD COLUMN IF NOT EXISTS public_id UUID NOT NULL;

ALTER TABLE IF EXISTS reviews
ADD CONSTRAINT uq_user_product UNIQUE (user_id, product_id);

CREATE INDEX idx_reviews_public_id ON reviews(public_id);