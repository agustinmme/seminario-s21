USE cfp402;

USE cfp402;
INSERT INTO personas (dni, nombre, apellido, email, telefono, fecha_nacimiento, direccion, rol) VALUES
-- Profesores
('22333444', 'Juan', 'Perez', 'juan.perez@cfp402.edu.ar', '+54-11-1234-5678', '1978-05-15', 'San Martin 234', 'Profesor'),
('20987654', 'Maria', 'Gonzalez', 'maria.gonzalez@cfp402.edu.ar', '11-4455-6677', '1980-03-10', 'Av. Corrientes 789', 'Profesor'),
('25446789', 'Carlos', 'Rodriguez', 'carlos.rodriguez@cfp402.edu.ar', '11-7788-9900', '1985-11-05', 'Belgrano 456', 'Profesor'),
('23444555', 'Ana', 'Martinez', 'ana.martinez@cfp402.edu.ar', '+54-11-4567-8901', '1982-12-03', 'Libertad 567', 'Profesor'),
('24555666', 'Luis', 'Garcia', 'luis.garcia@cfp402.edu.ar', '+54-11-5678-9012', '1979-08-22', 'Independencia 890', 'Profesor'),
('25666777', 'Elena', 'Lopez', 'elena.lopez@cfp402.edu.ar', '+54-11-6789-0123', '1984-01-14', 'Rivadavia 123', 'Profesor'),
('26777888', 'Roberto', 'Fernandez', 'roberto.fernandez@cfp402.edu.ar', '+54-11-7890-1234', '1976-06-30', 'Moreno 456', 'Profesor'),
('27888999', 'Laura', 'Jimenez', 'laura.jimenez@cfp402.edu.ar', '+54-11-8901-2345', '1983-04-18', 'Sarmiento 789', 'Profesor'),
('28999000', 'Pedro', 'Silva', 'pedro.silva@cfp402.edu.ar', '+54-11-9012-3456', '1981-11-25', 'Belgrano 012', 'Profesor'),
('18765432', 'Carmen', 'Torres', 'carmen.torres@cfp402.edu.ar', '11-3344-5566', '1975-09-20', 'Mitre 123', 'Profesor'),

-- Alumnos (incluye los del ejemplo de GestionPersonasView)
('30123456', 'Ana', 'Martinez', 'ana.martinez@email.com', '11-4455-6677', '1990-05-15', 'Av. Libertador 1234', 'Alumno'),
('31234567', 'Luis', 'Garcia', 'luis.garcia@email.com', '11-5566-7788', '1992-07-22', 'Calle San Martin 567', 'Alumno'),
('12345678', 'Sofia', 'Martinez', 'sofia.martinez@email.com', '+54-11-1111-1111', '1995-03-15', 'Av. Corrientes 1234, CABA', 'Alumno'),
('23456789', 'Diego', 'Fernandez', 'diego.fernandez@email.com', '+54-11-2222-2222', '1992-07-22', 'San Martin 567, La Plata', 'Alumno'),
('34567890', 'Valentina', 'Rodriguez', 'valentina.rodriguez@email.com', '+54-11-3333-3333', '1998-11-08', 'Belgrano 890, Berisso', 'Alumno'),
('45678901', 'Mateo', 'Garcia', 'mateo.garcia@email.com', '+54-11-4444-4444', '1990-01-12', 'Rivadavia 345, Ensenada', 'Alumno'),
('56789012', 'Camila', 'Lopez', 'camila.lopez@email.com', '+54-11-5555-5555', '1997-09-30', 'Mitre 678, La Plata', 'Alumno'),
('67890123', 'Lucas', 'Gonzalez', 'lucas.gonzalez@email.com', '+54-11-6666-6666', '1994-05-18', '9 de Julio 123, CABA', 'Alumno'),
('78901234', 'Agustina', 'Silva', 'agustina.silva@email.com', '+54-11-7777-7777', '1996-12-03', 'San Juan 456, Quilmes', 'Alumno'),
('89012345', 'Franco', 'Torres', 'franco.torres@email.com', '+54-11-8888-8888', '1993-08-25', 'Moreno 789, Avellaneda', 'Alumno');

-- Insertar módulos
INSERT INTO modulos (nombre, descripcion, horas_duracion, orden_sugerido) VALUES
('Practica Basica', 'Introducción práctica a los conceptos fundamentales', 8, 1),
('Introduccion al Curso', 'Presentación general del programa de estudios', 4, 2),
('Fundamentos Basicos', 'Conceptos teóricos esenciales', 12, 3),
('Conceptos Intermedios', 'Desarrollo de conocimientos intermedios', 16, 4),
('Tecnicas Avanzadas', 'Técnicas especializadas del área', 20, 5),
('Practica Supervisada', 'Práctica con supervisión docente', 24, 6),
('Proyecto Final', 'Desarrollo de proyecto integrador', 20, 7),
('Evaluacion Teorica', 'Evaluación de conocimientos teóricos', 4, 8),
('Evaluacion Practica', 'Evaluación de habilidades prácticas', 8, 9),
('Trabajo en Equipo', 'Desarrollo de competencias colaborativas', 12, 10),
('Comunicacion Efectiva', 'Técnicas de comunicación profesional', 8, 11),
('Resolucion de Problemas', 'Metodologías para resolución de problemas', 12, 12),
('Liderazgo', 'Desarrollo de habilidades de liderazgo', 16, 13),
('Innovacion y Creatividad', 'Fomento del pensamiento creativo', 12, 14),
('Etica Profesional', 'Principios éticos en el ámbito profesional', 8, 15);

-- Insertar cursos
INSERT INTO cursos (codigo, nombre, descripcion, fecha_inicio, fecha_fin, profesor_id, especialidad, horas_totales, cupo_maximo) VALUES
('0001', 'Programación Web Básica', 'Curso introductorio de desarrollo web con HTML, CSS y JavaScript', '2025-03-01', '2025-06-30', 1, 'Programación y Desarrollo', 120, 15),
('0002', 'Diseño Gráfico Digital', 'Fundamentos del diseño gráfico utilizando herramientas digitales', '2025-02-15', '2025-07-15', 2, 'Diseño Gráfico', 150, 12),
('0003', 'Administración de Empresas', 'Conceptos básicos de administración y gestión empresarial', '2025-04-01', '2025-08-31', 3, 'Administración', 100, 20),
('0004', 'Contabilidad General', 'Principios fundamentales de contabilidad', '2025-03-15', '2025-09-15', 4, 'Contabilidad', 130, 18),
('0005', 'Marketing Digital', 'Estrategias de marketing en entornos digitales', '2025-05-01', '2025-10-31', 5, 'Marketing Digital', 110, 15);

-- Insertar relaciones curso-modulos
INSERT INTO curso_modulos (curso_id, modulo_id, orden_en_curso, horas_asignadas) VALUES
-- (Programación Web Básica)
(1, 2, 1, 4),   -- Introduccion al Curso
(1, 3, 2, 16),  -- Fundamentos Basicos
(1, 4, 3, 20),  -- Conceptos Intermedios
(1, 5, 4, 24),  -- Tecnicas Avanzadas
(1, 6, 5, 28),  -- Practica Supervisada
(1, 7, 6, 24),  -- Proyecto Final
(1, 9, 7, 4),   -- Evaluacion Practica

-- (Diseño Gráfico Digital)
(2, 2, 1, 4),   -- Introduccion al Curso
(2, 1, 2, 12),  -- Practica Basica
(2, 3, 3, 20),  -- Fundamentos Basicos
(2, 4, 4, 24),  -- Conceptos Intermedios
(2, 14, 5, 20), -- Innovacion y Creatividad
(2, 7, 6, 30),  -- Proyecto Final
(2, 8, 7, 8),   -- Evaluacion Teorica
(2, 9, 8, 12),  -- Evaluacion Practica
(2, 11, 9, 16), -- Comunicacion Efectiva
(2, 15, 10, 4), -- Etica Profesional

-- (Administración de Empresas)
(3, 2, 1, 4),   -- Introduccion al Curso
(3, 3, 2, 16),  -- Fundamentos Basicos
(3, 4, 3, 20),  -- Conceptos Intermedios
(3, 10, 4, 16), -- Trabajo en Equipo
(3, 11, 5, 12), -- Comunicacion Efectiva
(3, 12, 6, 16), -- Resolucion de Problemas
(3, 13, 7, 16), -- Liderazgo

-- (Contabilidad General)
(4, 2, 1, 4),   -- Introduccion al Curso
(4, 3, 2, 20),  -- Fundamentos Basicos
(4, 4, 3, 24),  -- Conceptos Intermedios
(4, 5, 4, 28),  -- Tecnicas Avanzadas
(4, 6, 5, 30),  -- Practica Supervisada
(4, 8, 6, 8),   -- Evaluacion Teorica
(4, 9, 7, 8),   -- Evaluacion Practica
(4, 15, 8, 8),  -- Etica Profesional

-- (Marketing Digital)
(5, 2, 1, 4),   -- Introduccion al Curso
(5, 3, 2, 16),  -- Fundamentos Basicos
(5, 4, 3, 20),  -- Conceptos Intermedios
(5, 14, 4, 16), -- Innovacion y Creatividad
(5, 11, 5, 16), -- Comunicacion Efectiva
(5, 7, 6, 20),  -- Proyecto Final
(5, 9, 7, 8),   -- Evaluacion Practica
(5, 15, 8, 10); -- Etica Profesional

-- Insertar inscripciones de ejemplo
INSERT INTO inscripciones (alumno_id, curso_id, estado, certificado_generado) VALUES
(13, 1, 'aprobado', TRUE),   -- Sofia en Programación Web
(15, 2, 'aprobado', TRUE),   -- Valentina en Diseño Gráfico
(19, 1, 'aprobado', FALSE),  -- Agustina en Programación Web
(11, 4, 'aprobado', TRUE),   -- Ana Martinez en Contabilidad
(12, 5, 'abandono', FALSE),  -- Luis Garcia en Marketing
(14, 1, 'aprobado', FALSE),  -- Diego en Programación Web  
(16, 2, 'abandono', FALSE),  -- Mateo en Diseño Gráfico
(17, 1, 'aprobado', TRUE),   -- Camila en Programación Web
(18, 3, 'aprobado', FALSE),  -- Lucas en Administración
(20, 2, 'aprobado', TRUE);   -- Franco en Diseño Gráfico

-- Insertar seguimiento de modulos por alumno (ejemplos para alumnos aprobados)
INSERT INTO alumno_modulos (inscripcion_id, modulo_id, estado, nota, fecha_aprobacion) VALUES
-- Sofia en Programación Web (inscripcion_id = 1)
(1, 2, 'aprobado', 8.5, '2025-03-15'),
(1, 3, 'aprobado', 9.0, '2025-04-01'),
(1, 4, 'aprobado', 8.0, '2025-04-20'),
(1, 5, 'aprobado', 8.5, '2025-05-10'),
(1, 6, 'aprobado', 9.5, '2025-06-01'),
(1, 7, 'aprobado', 8.8, '2025-06-15'),
(1, 9, 'aprobado', 9.0, '2025-06-25'),

-- Valentina en Diseño Gráfico (inscripcion_id = 2)
(2, 2, 'aprobado', 9.2, '2024-03-01'),
(2, 1, 'aprobado', 8.8, '2024-03-15'),
(2, 3, 'aprobado', 9.0, '2024-04-05'),
(2, 4, 'aprobado', 8.5, '2024-04-25'),
(2, 14, 'aprobado', 9.5, '2024-05-15'),
(2, 7, 'aprobado', 9.0, '2024-06-10'),
(2, 8, 'aprobado', 8.0, '2024-06-20'),
(2, 9, 'aprobado', 8.8, '2024-07-01'),
(2, 11, 'aprobado', 9.2, '2024-07-10'),
(2, 15, 'aprobado', 8.5, '2024-07-15');

-- Insertar certificados para algunos alumnos aprobados
INSERT INTO certificados (inscripcion_id, tipo, numero_certificado, generado_por) VALUES
(1, 'digital', 'CFP402-PROG001-2024-001', 'Sistema CFP'),
(2, 'digital', 'CFP402-DIS001-2024-001', 'Sistema CFP'),
(4, 'fisico', 'CFP402-CONT001-2024-001', 'Sistema CFP'),
(8, 'digital', 'CFP402-PROG001-2024-002', 'Sistema CFP'),
(10, 'digital', 'CFP402-DIS001-2024-002', 'Sistema CFP');