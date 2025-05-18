DROP database IF EXISTS CFP402;
CREATE DATABASE IF NOT EXISTS CFP402;
USE cfp402;

-- Tabla persona
CREATE TABLE persona (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  apellido VARCHAR(100) NOT NULL,
  dni VARCHAR(255) NOT NULL,
  genero VARCHAR(50) NOT NULL,
  rol VARCHAR(50) NOT NULL,
  tell VARCHAR(50),
   UNIQUE (dni) 
);

-- Tabla curso
CREATE TABLE curso (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(200) NOT NULL,
  numero_curso VARCHAR(30) NOT NULL,
  turno VARCHAR(40) NOT NULL,
  fecha_inicio DATE,
  fecha_fin DATE
);

-- Tabla modulo
CREATE TABLE modulo (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(200) NOT NULL,
  horas VARCHAR(200) NOT NULL
);

-- Tabla certificado
CREATE TABLE certificado (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  numero_institucion VARCHAR(100) NOT NULL,
  distrito VARCHAR(100) NOT NULL,
  es_acreditacion BOOLEAN NOT NULL,
  numero_egresado VARCHAR(50) NOT NULL,
  curso_id BIGINT NOT NULL,
  persona_id BIGINT NOT NULL,
  FOREIGN KEY (curso_id) REFERENCES curso(id),
  FOREIGN KEY (persona_id) REFERENCES persona(id),
  UNIQUE (numero_egresado) 
);

-- Tabla inscripcion
CREATE TABLE inscripcion (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  persona_id BIGINT NOT NULL,
  curso_id BIGINT NOT NULL,
  FOREIGN KEY (persona_id) REFERENCES persona(id),
  FOREIGN KEY (curso_id) REFERENCES curso(id)
);

-- Tabla curso_modulo 
CREATE TABLE curso_modulo (
  curso_id BIGINT NOT NULL,
  modulo_id BIGINT NOT NULL,
  PRIMARY KEY (curso_id, modulo_id),
  FOREIGN KEY (curso_id) REFERENCES curso(id),
  FOREIGN KEY (modulo_id) REFERENCES modulo(id)
);

-- Tabla inscripcion_modulo 
CREATE TABLE inscripcion_modulo (
  inscripcion_id BIGINT NOT NULL,
  modulo_id BIGINT NOT NULL,
  PRIMARY KEY (inscripcion_id, modulo_id),
  FOREIGN KEY (inscripcion_id) REFERENCES inscripcion(id),
  FOREIGN KEY (modulo_id) REFERENCES modulo(id)
);

-- Tabla curso_profesor 
CREATE TABLE curso_profesor (
  curso_id BIGINT NOT NULL,
  persona_id BIGINT NOT NULL,
  PRIMARY KEY (curso_id, persona_id),
  FOREIGN KEY (curso_id) REFERENCES curso(id),
  FOREIGN KEY (persona_id) REFERENCES persona(id)
);