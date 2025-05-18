USE cfp402;

-- Datos de ejemplo para poblar las tablas
-- Creo personas (3 alumnos y 1 profesor)
INSERT INTO persona (nombre, apellido, dni, genero, rol, tell) VALUES 
('Agustin', 'Mansilla', '40145625', 'Masculino', 'Alumno', ''),
('Juan', 'Rodríguez', '36123456', 'Masculino', 'Alumno', ''),
('Lucia', 'Fernandez', '37456789', 'Femenino', 'Alumno', ''),
('Carlos', 'Martins', '25678901', 'Masculino', 'Profesor', '2216453212');

-- Creo un curso
INSERT INTO curso (nombre, numero_curso, turno, fecha_inicio, fecha_fin) VALUES 
('Desarrollo Web Full Stack', '01365', 'Tarde', '2025-05-18', '2025-07-15');

-- Creo modulos
INSERT INTO modulo (nombre, horas) VALUES 
('HTML y CSS', '40'),
('JavaScript', '60'),
('Base de datos', '50'),
('Frameworks', '70');

-- Agrego a el curso con sus modulos
INSERT INTO curso_modulo (curso_id, modulo_id) VALUES 
(1, 1),(1, 2),(1, 3),(1, 4);

-- Agrego a el curso con el profesor
INSERT INTO curso_profesor (curso_id, persona_id) VALUES 
(1, 4);

-- Agrego a los alumnos en el curso
INSERT INTO inscripcion (persona_id, curso_id) VALUES 
(1, 1),(2, 1),(3, 1);


-- Agrego los modulos aprobados a cada alumno
INSERT INTO inscripcion_modulo (inscripcion_id, modulo_id) VALUES 
-- alumno 1
(1, 1),(1, 2),(1, 3),(1, 4),
-- alumno 2
(2, 1),(2, 2),(2, 3),(2, 4),
-- alumno 3
(3, 1),(3, 2),(3, 3),(3, 4);

-- Creo certificados para los alumnos que completaron el curso
INSERT INTO certificado (numero_institucion, distrito, es_acreditacion, numero_egresado, curso_id, persona_id) VALUES 
('402', 'Berisso', false, '28000', 1, 1),
('402', 'Berisso', false, '28001', 1, 2),
('402', 'Berisso', false, '28002', 1, 3);