CREATE SCHEMA IF NOT EXISTS polideportivo;
SET search_path TO polideportivo;

CREATE TABLE usuario (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    primer_apellido VARCHAR(100) NOT NULL,
    segundo_apellido VARCHAR(100),
    correo VARCHAR(150) UNIQUE,
    rol VARCHAR(20) NOT NULL,

    CONSTRAINT chk_usuario_rol
        CHECK (rol IN ('ADMINISTRADOR', 'CLIENTE'))
);

CREATE TABLE disciplina (
    id_disciplina SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT
);

CREATE TABLE espacio (
    id_espacio SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT NOT NULL,
    id_disciplina INT NOT NULL,
    capacidad INT NOT NULL,
    hora_apertura TIME NOT NULL,
    hora_cierre TIME NOT NULL,
    precio_hora NUMERIC(10,2) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_espacio_disciplina
        FOREIGN KEY (id_disciplina) REFERENCES disciplina(id_disciplina),

    CONSTRAINT chk_espacio_capacidad
        CHECK (capacidad > 0),

    CONSTRAINT chk_espacio_precio
        CHECK (precio_hora >= 0),

    CONSTRAINT chk_espacio_horario
        CHECK (hora_apertura < hora_cierre)
);
CREATE INDEX idx_espacio_disciplina ON espacio(id_disciplina);

CREATE TABLE reserva (
    id_reserva SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_espacio INT NOT NULL,
    cantidad_personas INT NOT NULL,
    fecha_reserva DATE NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado VARCHAR(30) NOT NULL,
    monto_total NUMERIC(10,2) NOT NULL,

    CONSTRAINT fk_reserva_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),

    CONSTRAINT fk_reserva_espacio
        FOREIGN KEY (id_espacio) REFERENCES espacio(id_espacio),

    CONSTRAINT chk_reserva_cantidad_personas
        CHECK (cantidad_personas > 0),

    CONSTRAINT chk_reserva_horario
        CHECK (hora_inicio < hora_fin),

    CONSTRAINT chk_reserva_estado
        CHECK (estado IN ('PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'FINALIZADA')),

    CONSTRAINT chk_reserva_monto
        CHECK (monto_total >= 0)
);
CREATE INDEX idx_reserva_usuario_estado ON reserva(id_usuario,estado);
CREATE INDEX idx_reserva_disponibilidad ON reserva(id_espacio,fecha_reserva,hora_inicio,hora_fin);

CREATE TABLE pago (
    id_pago SERIAL PRIMARY KEY,
    id_reserva INT NOT NULL,
    monto NUMERIC(10,2) NOT NULL,
    metodo_pago VARCHAR(30) NOT NULL,
    estado_pago VARCHAR(30) NOT NULL,
    fecha_pago TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_pago_reserva
        FOREIGN KEY (id_reserva) REFERENCES reserva(id_reserva),

    CONSTRAINT chk_pago_monto
        CHECK (monto > 0),

    CONSTRAINT chk_pago_metodo
        CHECK (metodo_pago IN ('EFECTIVO', 'SINPE', 'TARJETA')),

    CONSTRAINT chk_pago_estado
        CHECK (estado_pago IN ('PENDIENTE', 'APROBADO')),

    CONSTRAINT uq_pago_reserva
        UNIQUE (id_reserva)
);

CREATE TABLE equipamiento (
    id_equipamiento SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    cantidad_total INT NOT NULL,
    id_disciplina INT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_equipamiento_disciplina
        FOREIGN KEY (id_disciplina) REFERENCES disciplina(id_disciplina),

    CONSTRAINT chk_equipamiento_cantidad
        CHECK (cantidad_total >= 0)
);
CREATE INDEX idx_equipamiento_disciplina ON equipamiento(id_disciplina);

CREATE TABLE reserva_equipamiento (
    id_reserva_equipamiento SERIAL PRIMARY KEY,
    id_reserva INT NOT NULL,
    id_equipamiento INT NOT NULL,
    cantidad INT NOT NULL,

    CONSTRAINT fk_reserva_equipamiento_reserva
        FOREIGN KEY (id_reserva) REFERENCES reserva(id_reserva),

    CONSTRAINT fk_reserva_equipamiento_equipamiento
        FOREIGN KEY (id_equipamiento) REFERENCES equipamiento(id_equipamiento),

    CONSTRAINT uq_reserva_equipamiento
        UNIQUE (id_reserva, id_equipamiento),

    CONSTRAINT chk_reserva_equipamiento_cantidad
        CHECK (cantidad > 0)
);