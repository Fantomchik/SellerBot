CREATE TABLE IF NOT EXISTS phone_price (
    id SERIAL PRIMARY KEY,
    brand VARCHAR(64) NOT NULL,
    model VARCHAR(255) NOT NULL,
    storage_gb INTEGER NOT NULL,
    base_price INTEGER NOT NULL,
    min_price INTEGER NOT NULL,
    max_price INTEGER NOT NULL,
    UNIQUE (brand, model, storage_gb)
);
