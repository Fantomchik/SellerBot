INSERT INTO phone_price (model, base_price, min_price, max_price) VALUES
    ('iPhone 13', 75000, 60000, 82000),
    ('iPhone 13 Pro', 90000, 75000, 99000),
    ('iPhone 14 Pro', 105000, 85000, 115000),
    ('Samsung Galaxy S23', 85000, 70000, 95000),
    ('Samsung Galaxy S23 Ultra', 110000, 90000, 125000),
    ('Xiaomi Redmi Note 12', 25000, 18000, 30000)
ON CONFLICT (model) DO NOTHING;
