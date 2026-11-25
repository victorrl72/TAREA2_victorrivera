CREATE DATABASE IF NOT EXISTS clinica_victorrivera
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE clinica_victorrivera;

-- ==========================================
-- 1. EMPLEADOS Y ESPECIALIDADES
-- ==========================================
CREATE TABLE IF NOT EXISTS empleado (
    id_empleado BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_apellidos VARCHAR(150) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    nif_nie VARCHAR(15) NOT NULL UNIQUE,
    direccion VARCHAR(200) NOT NULL,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    perfil ENUM('SECRETARIADO', 'ENFERMERIA', 'CIRUGIA') NOT NULL
);

CREATE TABLE IF NOT EXISTS secretariado (
    id_empleado BIGINT PRIMARY KEY,
    anios_experiencia INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_sec_emp FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS enfermeria (
    id_empleado BIGINT PRIMARY KEY,
    categoria CHAR(1) NOT NULL,
    CONSTRAINT fk_enf_emp FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado) ON DELETE CASCADE,
    CONSTRAINT chk_categoria CHECK (categoria IN ('A', 'B', 'C'))
);

CREATE TABLE IF NOT EXISTS cirujano (
    id_empleado BIGINT PRIMARY KEY,
    CONSTRAINT fk_cir_emp FOREIGN KEY (id_empleado) REFERENCES empleado(id_empleado) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS especialidad (
    id_especialidad BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS cirujano_especialidad (
    id_cirujano BIGINT NOT NULL,
    id_especialidad BIGINT NOT NULL,
    PRIMARY KEY (id_cirujano, id_especialidad),
    CONSTRAINT fk_ce_cir FOREIGN KEY (id_cirujano) REFERENCES cirujano(id_empleado) ON DELETE CASCADE,
    CONSTRAINT fk_ce_esp FOREIGN KEY (id_especialidad) REFERENCES especialidad(id_especialidad) ON DELETE CASCADE
);

-- ==========================================
-- 2. PACIENTES E HISTORIAL (Alergias en ficheros binarios)
-- ==========================================
CREATE TABLE IF NOT EXISTS paciente (
    id_paciente BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_completo VARCHAR(150) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    nif_nie VARCHAR(15) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS historial (
    num_historia BIGINT AUTO_INCREMENT PRIMARY KEY,
    descripcion TEXT,
    id_paciente BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_hist_pac FOREIGN KEY (id_paciente) REFERENCES paciente(id_paciente) ON DELETE CASCADE
);

-- Guarda solo el id_alergia que corresponde al fichero binario
CREATE TABLE IF NOT EXISTS historial_alergia (
    num_historia BIGINT NOT NULL,
    id_alergia BIGINT NOT NULL,
    PRIMARY KEY (num_historia, id_alergia),
    CONSTRAINT fk_ha_hist FOREIGN KEY (num_historia) REFERENCES historial(num_historia) ON DELETE CASCADE
);

-- ==========================================
-- 3. TRATAMIENTOS Y CITAS
-- ==========================================
CREATE TABLE IF NOT EXISTS tratamiento (
    id_tratamiento BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    fecha_inicio DATE NOT NULL,
    consentimiento BOOLEAN NOT NULL DEFAULT FALSE,
    informe_global TEXT,
    id_paciente BIGINT NOT NULL,
    CONSTRAINT fk_trat_pac FOREIGN KEY (id_paciente) REFERENCES paciente(id_paciente) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cita (
    id_cita BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_hora DATETIME NOT NULL,
    anotacion TEXT NOT NULL,
    id_tratamiento BIGINT NOT NULL,
    id_enfermero BIGINT NOT NULL,
    CONSTRAINT fk_cita_trat FOREIGN KEY (id_tratamiento) REFERENCES tratamiento(id_tratamiento) ON DELETE CASCADE,
    CONSTRAINT fk_cita_enf FOREIGN KEY (id_enfermero) REFERENCES enfermeria(id_empleado)
);

CREATE TABLE IF NOT EXISTS revision (
    id_cita BIGINT PRIMARY KEY,
    CONSTRAINT fk_rev_cita FOREIGN KEY (id_cita) REFERENCES cita(id_cita) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS intervencion (
    id_cita BIGINT PRIMARY KEY,
    duracion_minutos INT NOT NULL,
    CONSTRAINT fk_int_cita FOREIGN KEY (id_cita) REFERENCES cita(id_cita) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS intervencion_cirujano (
    id_intervencion BIGINT NOT NULL,
    id_cirujano BIGINT NOT NULL,
    PRIMARY KEY (id_intervencion, id_cirujano),
    CONSTRAINT fk_ic_int FOREIGN KEY (id_intervencion) REFERENCES intervencion(id_cita) ON DELETE CASCADE,
    CONSTRAINT fk_ic_cir FOREIGN KEY (id_cirujano) REFERENCES cirujano(id_empleado)
);

-- ==========================================
-- 4. DATOS INICIALES (Especialidades + 3 empleados de prueba)
-- ==========================================
INSERT IGNORE INTO especialidad (id_especialidad, nombre) VALUES
(1, 'Odontología'),
(2, 'Fisioterapia'),
(3, 'Ginecología'),
(4, 'Cirugía plástica'),
(5, 'Digestivo'),
(6, 'Cirugía pediátrica'),
(7, 'Radiología'),
(8, 'Geriatría');

INSERT IGNORE INTO empleado (id_empleado, nombre_apellidos, telefono, email, nif_nie, direccion, usuario, password, perfil) VALUES
(1, 'Camila Sánchez', '600111222', 'camila@clinica.com', '11111111A', 'Calle Corrida 10, Gijón', 'camila', 'cam1las', 'SECRETARIADO'),
(2, 'Patricia González', '600333444', 'patricia@clinica.com', '22222222B', 'Av. de la Costa 25, Gijón', 'patrigc', 'patri', 'ENFERMERIA'),
(3, 'Carlos Méndez', '600555666', 'carlos@clinica.com', '33333333C', 'Calle Uría 5, Gijón', 'carlosm', 'carlos123', 'CIRUGIA');

INSERT IGNORE INTO secretariado (id_empleado, anios_experiencia) VALUES (1, 5);
INSERT IGNORE INTO enfermeria (id_empleado, categoria) VALUES (2, 'A');
INSERT IGNORE INTO cirujano (id_empleado) VALUES (3);
INSERT IGNORE INTO cirujano_especialidad (id_cirujano, id_especialidad) VALUES (3, 1), (3, 4);