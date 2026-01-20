ALTER TABLE phone_price
    ADD COLUMN IF NOT EXISTS model_key VARCHAR(255);

-- Backfill for existing rows. We normalize similarly to app logic:
-- - trim
-- - collapse whitespace
-- - lower-case
UPDATE phone_price
SET model_key = lower(regexp_replace(trim(model), '\s+', ' ', 'g'))
WHERE model_key IS NULL;

ALTER TABLE phone_price
    ALTER COLUMN model_key SET NOT NULL;

-- Main lookup key for prices (normalized model)
CREATE UNIQUE INDEX IF NOT EXISTS uq_phone_price_model_key
    ON phone_price (model_key);

