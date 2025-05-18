USE CFP402;
-- Consulta para ver personas registradas con todos sus datos personales
SELECT 
    id,nombre,apellido,dni,genero,rol,tell
FROM 
    persona
ORDER BY 
    apellido, nombre;

--  Consulta para ver informacion basica de los cursos
SELECT 
    c.id,
    c.nombre AS nombre_curso,
    c.numero_curso,
    c.turno,
    c.fecha_inicio,
    c.fecha_fin,
    GROUP_CONCAT(DISTINCT CONCAT(p.nombre, ' ', p.apellido) SEPARATOR ', ') AS profesores,
    COUNT(DISTINCT i.id) AS total_alumnos,
    COUNT(DISTINCT m.id) AS total_modulos
FROM 
    curso c
LEFT JOIN 
    curso_profesor cp ON c.id = cp.curso_id
LEFT JOIN 
    persona p ON cp.persona_id = p.id AND p.rol = 'Profesor'
LEFT JOIN 
    inscripcion i ON c.id = i.curso_id
LEFT JOIN 
    curso_modulo cm ON c.id = cm.curso_id
LEFT JOIN 
    modulo m ON cm.modulo_id = m.id
GROUP BY 
    c.id, c.nombre, c.numero_curso, c.turno, c.fecha_inicio, c.fecha_fin

-- Consulta para mostrar alumnos y los modulos que tiene aprobado cada uno
SELECT 
    p.id AS alumno_id,
    p.nombre,
    p.apellido,
    p.dni,
    c.nombre AS curso,
    m.nombre AS modulo,
    m.horas
FROM 
    persona p
JOIN 
    inscripcion i ON p.id = i.persona_id
JOIN 
    curso c ON i.curso_id = c.id
JOIN 
    inscripcion_modulo im ON i.id = im.inscripcion_id
JOIN 
    modulo m ON im.modulo_id = m.id
WHERE 
    p.rol = 'Alumno'
ORDER BY 
    p.apellido

-- Consulta para ver un resumen de los modulos aprobados por alumno para un curso especifico
SELECT 
    p.id AS alumno_id,
    p.nombre,
    p.apellido,
    c.nombre AS curso,
    COUNT(m.id) AS modulos_aprobados
FROM 
    persona p
JOIN 
    inscripcion i ON p.id = i.persona_id
JOIN 
    curso c ON i.curso_id = c.id
JOIN 
    inscripcion_modulo im ON i.id = im.inscripcion_id
JOIN 
    modulo m ON im.modulo_id = m.id
WHERE 
    p.rol = 'Alumno'
    AND c.id = 1  -- fijo el curso
GROUP BY 
    p.id, p.nombre, p.apellido, c.nombre
ORDER BY 
    p.apellido

-- Actualizar todos los datos de una persona 
UPDATE persona
SET nombre = 'Nuevo Nombre',
    apellido = 'Nuevo Apellido',
    dni = 'f4.322.111',
    genero = 'X',
    rol = 'Nuevo Rol',
    tell = '222222'
WHERE id = 1;

select * from persona

-- Actualizar todos los datos de un curso 
UPDATE curso
SET nombre = 'Nombre Actualizado',
    numero_curso = '123123',
    turno = 'Noche',
    fecha_inicio = '2025-08-01',
    fecha_fin = '2025-12-15'
WHERE id = 1;

select * from curso