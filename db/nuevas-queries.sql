USE cfp402;

-- Obtener informacion de modulos
SELECT m.nombre, m.descripcion, 
       COALESCE(cm.horas_asignadas, m.horas_duracion, 12) as horas 
FROM curso_modulos cm 
JOIN modulos m ON cm.modulo_id = m.id 
WHERE cm.curso_id = ? -- Reemplaza ? con el ID del curso
ORDER BY cm.orden_en_curso;

-- Obtener horas totales de un curso
SELECT COALESCE(SUM(COALESCE(cm.horas_asignadas, m.horas_duracion, 12)), 0) as total_horas 
FROM curso_modulos cm 
JOIN modulos m ON cm.modulo_id = m.id 
WHERE cm.curso_id = ?; -- Reemplaza ? con el ID del curso

-- Contar modulos de un curso
SELECT COUNT(*) as total 
FROM curso_modulos 
WHERE curso_id = ?; -- Reemplaza ? con el ID del curso

-- Insertar modulos aprobados 
INSERT INTO alumno_modulos (inscripcion_id, modulo_id, estado, fecha_aprobacion) 
SELECT ?, cm.modulo_id, 'aprobado', CURRENT_TIMESTAMP 
FROM curso_modulos cm 
WHERE cm.curso_id = ? 
ORDER BY cm.orden_en_curso 
LIMIT ?;
-- Parámetros: inscripcion_id, curso_id, cantidad_modulos

-- Obtener nombres de modulos por curso
SELECT m.nombre 
FROM curso_modulos cm 
JOIN modulos m ON cm.modulo_id = m.id 
WHERE cm.curso_id = ? -- Reemplaza ? con el ID del curso
ORDER BY cm.orden_en_curso;

-- Obtener todas las personas ordenadas
SELECT * FROM personas 
ORDER BY apellido, nombre;

-- Obtener solo profesores
SELECT * FROM personas 
WHERE rol = 'Profesor' 
ORDER BY apellido, nombre;

-- Obtener solo alumnos
SELECT * FROM personas 
WHERE rol = 'Alumno' 
ORDER BY apellido, nombre;

-- Buscar personas por DNI
SELECT * FROM personas 
WHERE dni LIKE ?; -- Reemplaza ? con '%dni%'


-- Ver modulos del curso 1
SELECT m.nombre, m.descripcion, 
       COALESCE(cm.horas_asignadas, m.horas_duracion, 12) as horas 
FROM curso_modulos cm 
JOIN modulos m ON cm.modulo_id = m.id 
WHERE cm.curso_id = 1
ORDER BY cm.orden_en_curso;

-- Ver horas totales del curso 1
SELECT COALESCE(SUM(COALESCE(cm.horas_asignadas, m.horas_duracion, 12)), 0) as total_horas 
FROM curso_modulos cm 
JOIN modulos m ON cm.modulo_id = m.id 
WHERE cm.curso_id = 1;

-- Contar modulos del curso 1
SELECT COUNT(*) as total 
FROM curso_modulos 
WHERE curso_id = 1;


-- Ver módulos del curso 2
SELECT m.nombre, m.descripcion, 
       COALESCE(cm.horas_asignadas, m.horas_duracion, 12) as horas 
FROM curso_modulos cm 
JOIN modulos m ON cm.modulo_id = m.id 
WHERE cm.curso_id = 2
ORDER BY cm.orden_en_curso;

-- Ver modulos del curso 3
SELECT m.nombre, m.descripcion, 
       COALESCE(cm.horas_asignadas, m.horas_duracion, 12) as horas 
FROM curso_modulos cm 
JOIN modulos m ON cm.modulo_id = m.id 
WHERE cm.curso_id = 3
ORDER BY cm.orden_en_curso;

-- Buscar persona con DNI especifico (ejemplo Sofia Martinez)
SELECT * FROM personas 
WHERE dni LIKE '%12345678%';

-- Buscar persona con DNI especifico (ejemplo Ana Martinez)
SELECT * FROM personas 
WHERE dni LIKE '%30123456%';

-- 11. Ejemplo de INSERT de módulos aprobados para Sofia (inscripcion_id = 1)
INSERT INTO alumno_modulos (inscripcion_id, modulo_id, estado, fecha_aprobacion) 
SELECT 1, cm.modulo_id, 'aprobado', CURRENT_TIMESTAMP 
FROM curso_modulos cm 
WHERE cm.curso_id = 1 
ORDER BY cm.orden_en_curso 
LIMIT 3;

-- Ver todas las inscripciones con datos completos
SELECT i.id as inscripcion_id, p.dni, p.nombre, p.apellido, c.codigo, c.nombre as curso
FROM inscripciones i
JOIN personas p ON i.alumno_id = p.id
JOIN cursos c ON i.curso_id = c.id
ORDER BY c.codigo, p.apellido;

-- Ver que modulos tiene aprobados Sofia en Programación Web
SELECT p.nombre, p.apellido, c.codigo, m.nombre as modulo, am.estado, am.fecha_aprobacion
FROM alumno_modulos am
JOIN inscripciones i ON am.inscripcion_id = i.id
JOIN personas p ON i.alumno_id = p.id
JOIN cursos c ON i.curso_id = c.id
JOIN modulos m ON am.modulo_id = m.id
WHERE p.dni = '12345678' AND c.codigo = '0001'
ORDER BY am.fecha_aprobacion;