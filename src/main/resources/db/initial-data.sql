CREATE TABLE IF NOT EXISTS configuracion (id BIGSERIAL PRIMARY KEY, nombre_parqueadero VARCHAR(255) NOT NULL, moneda VARCHAR(3) DEFAULT 'USD', logo_path VARCHAR(255), direccion VARCHAR(255), filas_defecto INT DEFAULT 5, columnas_defecto INT DEFAULT 10);

CREATE TABLE IF NOT EXISTS tarifa (id BIGSERIAL PRIMARY KEY, tipo_vehiculo VARCHAR(20) NOT NULL UNIQUE, precio_por_hora DECIMAL(10,2) NOT NULL, fraccion_minutos INT NOT NULL, configuracion_id BIGINT NOT NULL REFERENCES configuracion(id));

CREATE TABLE IF NOT EXISTS parqueadero (id BIGSERIAL PRIMARY KEY, nombre VARCHAR(100) NOT NULL, capacidad INT NOT NULL, configuracion_id BIGINT NOT NULL UNIQUE REFERENCES configuracion(id));

CREATE TABLE IF NOT EXISTS mapa_parqueadero (id BIGSERIAL PRIMARY KEY, filas INT NOT NULL, columnas INT NOT NULL, parqueadero_id BIGINT NOT NULL UNIQUE REFERENCES parqueadero(id));

CREATE TABLE IF NOT EXISTS espacio_parqueadero (id BIGSERIAL PRIMARY KEY, numero INT NOT NULL, fila INT NOT NULL, columna INT NOT NULL, estado VARCHAR(20) NOT NULL DEFAULT 'LIBRE', id_registro_activo VARCHAR(50), mapa_parqueadero_id BIGINT NOT NULL REFERENCES mapa_parqueadero(id), parqueadero_id BIGINT NOT NULL REFERENCES parqueadero(id));

CREATE TABLE IF NOT EXISTS vehiculo (id BIGSERIAL PRIMARY KEY, placa VARCHAR(20) NOT NULL UNIQUE, nombre_conductor VARCHAR(100) NOT NULL, tipo VARCHAR(20));

CREATE TABLE IF NOT EXISTS registro (id BIGSERIAL PRIMARY KEY, uuid VARCHAR(50) NOT NULL UNIQUE, vehiculo_id BIGINT NOT NULL REFERENCES vehiculo(id), espacio_id BIGINT NOT NULL REFERENCES espacio_parqueadero(id), fecha_hora_entrada TIMESTAMP NOT NULL, fecha_hora_salida TIMESTAMP, duracion_minutos BIGINT, total_cobrado DECIMAL(10,2), placa VARCHAR(20), conductor VARCHAR(100), tipo_vehiculo VARCHAR(20), espacio_etiqueta VARCHAR(20), moneda VARCHAR(10) DEFAULT 'USD', parqueadero_id BIGINT NOT NULL REFERENCES parqueadero(id));

INSERT INTO configuracion (id, nombre_parqueadero, moneda, logo_path, direccion, filas_defecto, columnas_defecto) VALUES (1, 'AutoManager', 'USD', '/images/logo.png', 'Av. Principal #123 - Sector Norte', 5, 10) ON CONFLICT (id) DO NOTHING;

INSERT INTO tarifa (id, tipo_vehiculo, precio_por_hora, fraccion_minutos, configuracion_id) VALUES (1, 'AUTOMOVIL', 1.00, 30, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO tarifa (id, tipo_vehiculo, precio_por_hora, fraccion_minutos, configuracion_id) VALUES (2, 'MOTO', 0.50, 30, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO tarifa (id, tipo_vehiculo, precio_por_hora, fraccion_minutos, configuracion_id) VALUES (3, 'CAMIONETA', 1.50, 30, 1) ON CONFLICT (id) DO NOTHING;

INSERT INTO parqueadero (id, nombre, capacidad, configuracion_id) VALUES (1, 'AutoManager', 50, 1) ON CONFLICT (id) DO NOTHING;

INSERT INTO mapa_parqueadero (id, filas, columnas, parqueadero_id) VALUES (1, 5, 10, 1) ON CONFLICT (id) DO NOTHING;

INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (1, 1, 0, 0, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (2, 2, 0, 1, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (3, 3, 0, 2, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (4, 4, 0, 3, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (5, 5, 0, 4, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (6, 6, 0, 5, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (7, 7, 0, 6, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (8, 8, 0, 7, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (9, 9, 0, 8, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (10, 10, 0, 9, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (11, 11, 1, 0, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (12, 12, 1, 1, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (13, 13, 1, 2, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (14, 14, 1, 3, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (15, 15, 1, 4, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (16, 16, 1, 5, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (17, 17, 1, 6, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (18, 18, 1, 7, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (19, 19, 1, 8, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (20, 20, 1, 9, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (21, 21, 2, 0, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (22, 22, 2, 1, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (23, 23, 2, 2, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (24, 24, 2, 3, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (25, 25, 2, 4, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (26, 26, 2, 5, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (27, 27, 2, 6, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (28, 28, 2, 7, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (29, 29, 2, 8, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (30, 30, 2, 9, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (31, 31, 3, 0, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (32, 32, 3, 1, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (33, 33, 3, 2, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (34, 34, 3, 3, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (35, 35, 3, 4, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (36, 36, 3, 5, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (37, 37, 3, 6, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (38, 38, 3, 7, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (39, 39, 3, 8, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (40, 40, 3, 9, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (41, 41, 4, 0, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (42, 42, 4, 1, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (43, 43, 4, 2, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (44, 44, 4, 3, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (45, 45, 4, 4, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (46, 46, 4, 5, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (47, 47, 4, 6, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (48, 48, 4, 7, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (49, 49, 4, 8, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;
INSERT INTO espacio_parqueadero (id, numero, fila, columna, estado, mapa_parqueadero_id, parqueadero_id) VALUES (50, 50, 4, 9, 'LIBRE', 1, 1) ON CONFLICT (id) DO NOTHING;

SELECT setval('configuracion_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM configuracion), 1), true);
SELECT setval('tarifa_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM tarifa), 1), true);
SELECT setval('parqueadero_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM parqueadero), 1), true);
SELECT setval('mapa_parqueadero_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM mapa_parqueadero), 1), true);
SELECT setval('espacio_parqueadero_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM espacio_parqueadero), 1), true);
SELECT setval('registro_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM registro), 1), true);
SELECT setval('vehiculo_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM vehiculo), 1), true);
