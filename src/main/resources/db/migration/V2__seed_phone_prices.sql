INSERT INTO phone_price (brand, model, storage_gb, base_price, min_price, max_price) VALUES
    ('Apple', '13', 128, 75000, 60000, 82000),
    ('Apple', '13 Pro', 256, 90000, 75000, 99000),
    ('Apple', '14 Pro', 256, 105000, 85000, 115000),
    ('Samsung', 'Galaxy S23', 128, 85000, 70000, 95000),
    ('Samsung', 'Galaxy S23 Ultra', 256, 110000, 90000, 125000),
    ('Xiaomi', 'Redmi Note 12', 128, 25000, 18000, 30000)
ON CONFLICT (brand, model, storage_gb) DO NOTHING;
