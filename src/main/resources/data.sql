TRUNCATE TABLE products;

INSERT INTO products
(name, description, wholesale_price, retail_price, quantity, active, created_at, updated_at)
VALUES
    ('iPhone Case', 'Matte black protective case', 5.00, 12.99, 50, 1, NOW(), NOW()),
    ('USB-C Cable', '1m USB-C to USB-C cable', 2.50, 6.99, 200, 1, NOW(), NOW()),
    ('Wireless Mouse', '2.4G ergonomic mouse', 8.00, 19.99, 0, 1, NOW(), NOW()),   -- quantity=0：不会出现在 /all
    ('Old Model Charger', 'Legacy charger', 3.00, 7.99, 30, 0, NOW(), NOW());       -- active=0：不会出现在 /all
