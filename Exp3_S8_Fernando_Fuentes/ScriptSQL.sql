-- DROP a la BD si ya existe
DROP DATABASE IF EXISTS Cine_DB;

-- Crea la BD Cine_DB
CREATE DATABASE Cine_DB CHARACTER SET utf8mb4;
USE Cine_DB;

-- Elimina la tabla Cartelera
DROP TABLE IF EXISTS CARTELERA;

-- Crea la tabla
CREATE TABLE CARTELERA (
  id        INT AUTO_INCREMENT PRIMARY KEY,
  titulo    VARCHAR(150) NOT NULL,
  director  VARCHAR(50)  NOT NULL,
  anio      INT          NOT NULL,
  duracion  INT          NOT NULL,           -- minutos
  genero    ENUM('Accion','Drama','Comedia','Terror','Aventura','CienciaFiccion','Romance','Thriller') NOT NULL,
	-- Reglas de calidad de datos:
	CONSTRAINT chk_anio_min CHECK (anio >= 1900), -- Rango de anios
	CONSTRAINT chk_dur   CHECK (duracion BETWEEN 1 AND 999), -- Rango de duracion
	-- Evita duplicados evidentes: mismo título en el mismo año.
	UNIQUE KEY uq_titulo_anio (titulo, anio)
) ENGINE=InnoDB;

-- Datos de prueba
INSERT INTO Cartelera (titulo, director, anio, duracion, genero) VALUES
('Magenta: El Comienzo', 'A. López', 2023, 120, 'Accion'),
('Sueños de Campus', 'B. Romero', 2022, 98,  'Comedia'),
('Misión Imposible', 'F. Rodríguez', 2022, 125, 'Accion');
