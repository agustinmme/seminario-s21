DROP database IF EXISTS cfp402;
CREATE DATABASE IF NOT EXISTS cfp402;

USE cfp402;

-- Tabla personas (profesores y alumnos)
CREATE TABLE personas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE,
    direccion VARCHAR(300),
    telefono VARCHAR(20),
    email VARCHAR(150),
    rol ENUM('Alumno', 'Profesor') NOT NULL
);

-- Tabla de módulos disponibles
CREATE TABLE modulos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL UNIQUE,
    descripcion TEXT,
    horas_duracion INT DEFAULT 12,
    orden_sugerido INT
);

-- Tabla principal de cursos
CREATE TABLE cursos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    profesor_id INT NOT NULL,
    especialidad VARCHAR(200),
    horas_totales INT DEFAULT 120,
    cupo_maximo INT DEFAULT 15,
    
    FOREIGN KEY (profesor_id) REFERENCES personas(id) ON DELETE RESTRICT
);

-- Tabla de relacion curso modulo
CREATE TABLE curso_modulos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    curso_id INT NOT NULL,
    modulo_id INT NOT NULL,
    orden_en_curso INT NOT NULL,
    horas_asignadas INT DEFAULT 12,
    
    FOREIGN KEY (curso_id) REFERENCES cursos(id) ON DELETE CASCADE,
    FOREIGN KEY (modulo_id) REFERENCES modulos(id) ON DELETE RESTRICT,
    UNIQUE KEY uk_curso_modulo (curso_id, modulo_id),
    UNIQUE KEY uk_curso_orden (curso_id, orden_en_curso)
);

-- Tabla de inscripciones de alumnos a cursos
CREATE TABLE inscripciones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    alumno_id INT NOT NULL,
    curso_id INT NOT NULL,
    fecha_inscripcion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('aprobado', 'abandono') DEFAULT 'aprobado',
    certificado_generado BOOLEAN DEFAULT FALSE,
    fecha_certificado TIMESTAMP NULL,
    observaciones TEXT,
    
    FOREIGN KEY (alumno_id) REFERENCES personas(id) ON DELETE CASCADE,
    FOREIGN KEY (curso_id) REFERENCES cursos(id) ON DELETE CASCADE,
    UNIQUE KEY uk_alumno_curso (alumno_id, curso_id)
);

-- Tabla de seguimiento de módulos por alumno
CREATE TABLE alumno_modulos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    inscripcion_id INT NOT NULL,
    modulo_id INT NOT NULL,
    estado ENUM('aprobado', 'desaprobado') DEFAULT 'desaprobado',
    nota DECIMAL(4,2),
    fecha_aprobacion TIMESTAMP NULL,
    observaciones TEXT,
    
    FOREIGN KEY (inscripcion_id) REFERENCES inscripciones(id) ON DELETE CASCADE,
    FOREIGN KEY (modulo_id) REFERENCES modulos(id) ON DELETE RESTRICT,
    UNIQUE KEY uk_inscripcion_modulo (inscripcion_id, modulo_id)
);

-- Tabla de certificados generados
CREATE TABLE certificados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    inscripcion_id INT NOT NULL,
    tipo ENUM('digital', 'fisico', 'constancia') NOT NULL,
    numero_certificado VARCHAR(50) NOT NULL UNIQUE,
    fecha_generacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ruta_archivo VARCHAR(500),
    generado_por VARCHAR(100),
    
    FOREIGN KEY (inscripcion_id) REFERENCES inscripciones(id) ON DELETE CASCADE
);