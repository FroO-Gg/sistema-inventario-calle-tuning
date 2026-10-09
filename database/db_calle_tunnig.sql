
-- CALLE TUNING
-- Actualización de contraseñas de demostración
-- Base de datos: calle_tuning_db

BEGIN;

-- Habilitar funciones criptográficas de PostgreSQL.
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Actualizar contraseña del administrador.
UPDATE public.usuario AS u
SET password = crypt('admin123forever', gen_salt('bf', 10))
FROM public.rol AS r
WHERE u.rol_id = r.id
  AND u.username = 'Admin'
  AND r.nombre = 'ADMINISTRADOR';

-- Actualizar contraseña del cliente.
UPDATE public.usuario AS u
SET password = crypt('duelesonreir', gen_salt('bf', 10))
FROM public.rol AS r
WHERE u.rol_id = r.id
  AND u.username = 'Elwaso'
  AND r.nombre = 'CLIENTE';

COMMIT;

-- Verificar los usuarios y sus roles sin mostrar contraseñas.
SELECT
    u.id,
    u.username,
    r.nombre AS rol,
    u.estado
FROM public.usuario AS u
INNER JOIN public.rol AS r
    ON r.id = u.rol_id
WHERE u.username IN ('Admin', 'Elwaso')
ORDER BY u.id;
