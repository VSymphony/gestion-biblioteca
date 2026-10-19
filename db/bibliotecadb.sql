-- Base de datos del proyecto gestion-biblioteca
-- Ejecutar con: mysql -u root -p < db/bibliotecadb.sql

CREATE DATABASE IF NOT EXISTS bibliotecadb
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE bibliotecadb;

-- Roles del sistema (los usuarios se crean desde la app)
CREATE TABLE IF NOT EXISTS rol (
    idrol INT NOT NULL AUTO_INCREMENT,
    nomrol VARCHAR(255) NOT NULL,
    PRIMARY KEY (idrol),
    UNIQUE KEY uk_rol_nomrol (nomrol)
);

INSERT IGNORE INTO rol (idrol, nomrol) VALUES
    (1, 'ADMIN'),
    (2, 'BIBLIOTECARIO'),
    (3, 'LECTOR');
