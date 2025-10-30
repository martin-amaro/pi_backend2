INSERT INTO business (id, name) VALUES (1, 'GadPacks Inc');

INSERT INTO categories (name, description, active, business_id, created_at, updated_at)
VALUES ('Electrónica', 'Componentes y gadgets', true, 1, NOW(), NOW());
