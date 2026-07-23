-- Datos de ejemplo para desarrollo.
-- Se ejecuta en cada arranque; como la BD H2 es en memoria y se recrea al
-- iniciar, no hay riesgo de duplicados. Sirve para probar las vistas sin tener
-- que dar de alta registros a mano.

INSERT INTO empleado (nombre, apellidos, email, puesto, fecha_alta) VALUES
 ('Lucía',  'Gómez Ruiz',   'lucia.gomez@tienda.com', 'VENDEDOR', '2026-01-15'),
 ('Marco',  'Díaz León',    'marco.diaz@tienda.com',  'TECNICO',  '2026-03-02'),
 ('Sara',   'Núñez Vidal',  'sara.nunez@tienda.com',  'GERENTE',  '2025-11-20');

INSERT INTO cliente (nombre, apellidos, nif, email, telefono, direccion, activo, fecha_primera_compra) VALUES
 ('Andrés',  'Prieto Salas', '12345678Z', 'andres.prieto@correo.com', '600111222', 'Calle Mayor 3',  TRUE,  '2026-05-10'),
 ('Beatriz', 'Ramos Ortega', '87654321X', 'bea.ramos@correo.com',     '600333444', 'Av. del Sol 21', TRUE,  NULL),
 ('Carlos',  'Ferrer Gil',   '11223344L', 'carlos.ferrer@correo.com', '600555666', 'Plaza Nueva 8',  FALSE, '2025-09-01');

-- Catálogo: incluye un producto sin stock y otro retirado de la venta
-- (activo = FALSE) para comprobar cómo se muestran esos casos.
INSERT INTO producto (nombre, precio, stock, activo) VALUES
 ('Lavadora Bosch Serie 4',       449.00, 12, TRUE),
 ('Frigorífico Samsung No Frost', 799.50,  5, TRUE),
 ('Microondas Balay 20L',          89.90,  0, TRUE),
 ('Televisor LG OLED 55"',       1199.00,  3, TRUE),
 ('Secadora Beko (descatalogada)', 329.00, 0, FALSE);

-- Pedidos de ejemplo en distintos estados. Los ids referenciados (cliente,
-- empleado, producto) son los generados por los INSERT anteriores, en orden.
INSERT INTO pedido (cliente_id, empleado_id, fecha, estado, total) VALUES
 (1, 1, '2026-05-10', 'ENTREGADO',  538.90),
 (3, 2, '2025-09-01', 'CANCELADO',  799.50),
 (1, 3, '2026-07-18', 'CREADO',    1199.00);

-- El precio unitario se guarda copiado del producto en el momento de la compra.
INSERT INTO linea_pedido (pedido_id, producto_id, cantidad, precio_unitario) VALUES
 (1, 1, 1,  449.00),
 (1, 3, 1,   89.90),
 (2, 2, 1,  799.50),
 (3, 4, 1, 1199.00);
