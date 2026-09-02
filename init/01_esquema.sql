
-- ============================================================================
-- Proyecto : Asesor Turistico Ferroviario y Peatonal - MTC
-- Curso    : Ingenieria de Software - UNU
-- Motor    : PostgreSQL 15
-- Script   : 01_esquema.sql  (DDL - creacion de tablas)
--
-- Corresponde a la seccion 6.2 (Diseno Fisico) y 6.3 (Diccionario de Datos)
-- del documento del proyecto.
--
-- Convencion: tablas en snake_case minuscula; columnas con el prefijo
-- normalizado de 3-4 letras exigido por la catedra, entre comillas dobles
-- para preservar el uso de mayusculas en PostgreSQL.
-- ============================================================================
 
-- ----------------------------------------------------------------------------
-- 1. ROL  (seguridad)
-- ----------------------------------------------------------------------------
CREATE TABLE rol (
    "RolIdRol"        INTEGER GENERATED ALWAYS AS IDENTITY,
    "RolNombreRol"    VARCHAR(30)   NOT NULL,
    CONSTRAINT pk_rol            PRIMARY KEY ("RolIdRol"),
    CONSTRAINT uq_rol_nombre     UNIQUE ("RolNombreRol")
);
 
-- ----------------------------------------------------------------------------
-- 2. USUARIO  (seguridad)
-- ----------------------------------------------------------------------------
CREATE TABLE usuario (
    "UsuIdUsuario"      INTEGER GENERATED ALWAYS AS IDENTITY,
    "UsuNombreUsuario"  VARCHAR(50)   NOT NULL,
    "UsuContrasenia"    VARCHAR(100)  NOT NULL,
    "UsuNombre"         VARCHAR(50)   NOT NULL,
    "UsuApellidos"      VARCHAR(50)   NOT NULL,
    "UsuEmail"          VARCHAR(50)   NOT NULL,
    "UsuIdRol"          INTEGER       NOT NULL,
    "UsuEstado"         VARCHAR(10)   NOT NULL DEFAULT 'Activo',
    CONSTRAINT pk_usuario        PRIMARY KEY ("UsuIdUsuario"),
    CONSTRAINT uq_usuario_nombre UNIQUE ("UsuNombreUsuario"),
    CONSTRAINT uq_usuario_email  UNIQUE ("UsuEmail"),
    CONSTRAINT fk_usuario_rol    FOREIGN KEY ("UsuIdRol")
        REFERENCES rol ("RolIdRol") ON DELETE RESTRICT,
    CONSTRAINT ck_usuario_estado CHECK ("UsuEstado" IN ('Activo', 'Inactivo'))
);
 
-- ----------------------------------------------------------------------------
-- 3. TIPO_TURISMO  (parametrica - RNF-06 Escalabilidad)
-- ----------------------------------------------------------------------------
CREATE TABLE tipo_turismo (
    "TipIdTipoTurismo"  INTEGER GENERATED ALWAYS AS IDENTITY,
    "TipNombre"         VARCHAR(30)   NOT NULL,
    "TipDescripcion"    VARCHAR(150)  NULL,
    CONSTRAINT pk_tipo_turismo    PRIMARY KEY ("TipIdTipoTurismo"),
    CONSTRAINT uq_tipo_nombre     UNIQUE ("TipNombre")
);
 
-- ----------------------------------------------------------------------------
-- 4. ESTACION  (nucleo - fuente PeruRail)
-- ----------------------------------------------------------------------------
CREATE TABLE estacion (
    "EstIdEstacion"   INTEGER GENERATED ALWAYS AS IDENTITY,
    "EstCodigo"       VARCHAR(10)    NOT NULL,
    "EstNombre"       VARCHAR(80)    NOT NULL,
    "EstLatitud"      NUMERIC(9,6)   NOT NULL,
    "EstLongitud"     NUMERIC(9,6)   NOT NULL,
    "EstAltitud"      NUMERIC(6,2)   NULL,
    "EstCiudad"       VARCHAR(50)    NOT NULL,
    "EstEstado"       VARCHAR(10)    NOT NULL DEFAULT 'Activa',
    CONSTRAINT pk_estacion         PRIMARY KEY ("EstIdEstacion"),
    CONSTRAINT uq_estacion_codigo  UNIQUE ("EstCodigo"),
    CONSTRAINT ck_estacion_estado  CHECK ("EstEstado" IN ('Activa', 'Inactiva'))
);
 
-- ----------------------------------------------------------------------------
-- 5. SERVICIO_TREN  (integracion PeruRail)
-- ----------------------------------------------------------------------------
CREATE TABLE servicio_tren (
    "SerIdServicio"          INTEGER GENERATED ALWAYS AS IDENTITY,
    "SerHorarioSalida"       TIME           NOT NULL,
    "SerHorarioLlegada"      TIME           NOT NULL,
    "SerTiempoTransitoMin"   INTEGER        NOT NULL,
    "SerTarifa"              NUMERIC(7,2)   NOT NULL,
    "SerIdEstacionOrigen"    INTEGER        NOT NULL,
    "SerIdEstacionDestino"   INTEGER        NOT NULL,
    CONSTRAINT pk_servicio_tren        PRIMARY KEY ("SerIdServicio"),
    CONSTRAINT fk_servicio_est_origen  FOREIGN KEY ("SerIdEstacionOrigen")
        REFERENCES estacion ("EstIdEstacion") ON DELETE RESTRICT,
    CONSTRAINT fk_servicio_est_destino FOREIGN KEY ("SerIdEstacionDestino")
        REFERENCES estacion ("EstIdEstacion") ON DELETE RESTRICT,
    CONSTRAINT ck_servicio_tiempo      CHECK ("SerTiempoTransitoMin" > 0),
    CONSTRAINT ck_servicio_tarifa      CHECK ("SerTarifa" >= 0)
);
 
-- ----------------------------------------------------------------------------
-- 6. ZONA_TURISTICA  (fuente Travel Group Peru)
--
-- NOTA: la columna "ZonIdTipoTurismo" fue retirada de esta tabla. La
-- categorizacion turistica pasa a resolverse mediante la tabla intermedia
-- zona_tipo_turismo (punto 7), ya que una misma zona puede pertenecer a mas
-- de una categoria a la vez (relacion N:M).
-- ----------------------------------------------------------------------------
CREATE TABLE zona_turistica (
    "ZonIdZona"             INTEGER GENERATED ALWAYS AS IDENTITY,
    "ZonNombre"             VARCHAR(100)   NOT NULL,
    "ZonDescripcion"        VARCHAR(500)   NULL,
    "ZonIdEstacionCercana"  INTEGER        NOT NULL,
    "ZonCostoAprox"         NUMERIC(7,2)   NULL,
    "ZonCupoMaximoDiario"   INTEGER        NULL,
    "ZonEstado"             VARCHAR(10)    NOT NULL DEFAULT 'Activa',
    CONSTRAINT pk_zona_turistica    PRIMARY KEY ("ZonIdZona"),
    CONSTRAINT fk_zona_estacion     FOREIGN KEY ("ZonIdEstacionCercana")
        REFERENCES estacion ("EstIdEstacion") ON DELETE RESTRICT,
    CONSTRAINT ck_zona_estado       CHECK ("ZonEstado" IN ('Activa', 'Inactiva')),
    CONSTRAINT ck_zona_cupo         CHECK ("ZonCupoMaximoDiario" IS NULL
                                           OR "ZonCupoMaximoDiario" > 0)
);
 
-- ----------------------------------------------------------------------------
-- 7. ZONA_TIPO_TURISMO  (tabla intermedia N:M)
--
-- Resuelve la relacion muchos a muchos entre zona_turistica y tipo_turismo.
-- Ejemplo: la Fortaleza de Ollantaytambo puede clasificarse simultaneamente
-- como "Historia/Cultura" y como "Naturaleza", y debe aparecer en las
-- busquedas de ambas preferencias (RF-03).
-- ----------------------------------------------------------------------------
CREATE TABLE zona_tipo_turismo (
    "ZtiIdZonaTipo"        INTEGER GENERATED ALWAYS AS IDENTITY,
    "ZtiIdZonaTuristica"   INTEGER   NOT NULL,
    "ZtiIdTipoTurismo"     INTEGER   NOT NULL,
    CONSTRAINT pk_zona_tipo_turismo    PRIMARY KEY ("ZtiIdZonaTipo"),
    CONSTRAINT fk_zti_zona             FOREIGN KEY ("ZtiIdZonaTuristica")
        REFERENCES zona_turistica ("ZonIdZona") ON DELETE CASCADE,
    CONSTRAINT fk_zti_tipo             FOREIGN KEY ("ZtiIdTipoTurismo")
        REFERENCES tipo_turismo ("TipIdTipoTurismo") ON DELETE RESTRICT,
    CONSTRAINT uq_zti_zona_tipo        UNIQUE ("ZtiIdZonaTuristica",
                                               "ZtiIdTipoTurismo")
);
 
-- ----------------------------------------------------------------------------
-- 8. RUTA_PEATONAL  (motor de rutas - RNF-04 circuito cerrado)
-- ----------------------------------------------------------------------------
CREATE TABLE ruta_peatonal (
    "RutIdRuta"              INTEGER GENERATED ALWAYS AS IDENTITY,
    "RutNombre"              VARCHAR(100)   NOT NULL,
    "RutDistanciaKm"         NUMERIC(5,2)   NOT NULL,
    "RutTiempoEstimadoMin"   INTEGER        NOT NULL,
    "RutDificultad"          VARCHAR(10)    NOT NULL,
    "RutIdEstacionOrigen"    INTEGER        NOT NULL,
    "RutIdZonaDestino"       INTEGER        NOT NULL,
    "RutEsIdaVuelta"         BOOLEAN        NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_ruta_peatonal      PRIMARY KEY ("RutIdRuta"),
    CONSTRAINT fk_ruta_estacion      FOREIGN KEY ("RutIdEstacionOrigen")
        REFERENCES estacion ("EstIdEstacion") ON DELETE RESTRICT,
    CONSTRAINT fk_ruta_zona          FOREIGN KEY ("RutIdZonaDestino")
        REFERENCES zona_turistica ("ZonIdZona") ON DELETE RESTRICT,
    CONSTRAINT ck_ruta_dificultad    CHECK ("RutDificultad" IN ('Baja', 'Media', 'Alta')),
    CONSTRAINT ck_ruta_ida_vuelta    CHECK ("RutEsIdaVuelta" = TRUE),
    CONSTRAINT ck_ruta_distancia     CHECK ("RutDistanciaKm" > 0),
    CONSTRAINT ck_ruta_tiempo        CHECK ("RutTiempoEstimadoMin" > 0)
);
 
-- ----------------------------------------------------------------------------
-- 9. PREVISION_CLIMA  (integracion SENAMHI)
-- ----------------------------------------------------------------------------
CREATE TABLE prevision_clima (
    "CliIdClima"              INTEGER GENERATED ALWAYS AS IDENTITY,
    "CliFecha"                DATE           NOT NULL,
    "CliTemperaturaC"         NUMERIC(4,1)   NOT NULL,
    "CliProbabilidadLluvia"   NUMERIC(4,1)   NOT NULL,
    "CliEstadoClima"          VARCHAR(30)    NOT NULL,
    "CliIdEstacion"           INTEGER        NOT NULL,
    CONSTRAINT pk_prevision_clima    PRIMARY KEY ("CliIdClima"),
    CONSTRAINT fk_clima_estacion     FOREIGN KEY ("CliIdEstacion")
        REFERENCES estacion ("EstIdEstacion") ON DELETE RESTRICT,
    CONSTRAINT ck_clima_lluvia       CHECK ("CliProbabilidadLluvia" BETWEEN 0 AND 100),
    CONSTRAINT uq_clima_est_fecha    UNIQUE ("CliIdEstacion", "CliFecha")
);
 
-- ----------------------------------------------------------------------------
-- 10. INFORME_PLANIFICACION  (modulo de informes)
-- ----------------------------------------------------------------------------
CREATE TABLE informe_planificacion (
    "InfIdInforme"       INTEGER GENERATED ALWAYS AS IDENTITY,
    "InfCodigo"          VARCHAR(15)    NOT NULL,
    "InfFechaEmision"    TIMESTAMP      NOT NULL DEFAULT NOW(),
    "InfFechaVisita"     DATE           NOT NULL,
    "InfIdUsuario"       INTEGER        NULL,
    "InfIdRuta"          INTEGER        NOT NULL,
    "InfTotalEstimado"   NUMERIC(7,2)   NOT NULL,
    CONSTRAINT pk_informe            PRIMARY KEY ("InfIdInforme"),
    CONSTRAINT uq_informe_codigo     UNIQUE ("InfCodigo"),
    CONSTRAINT fk_informe_usuario    FOREIGN KEY ("InfIdUsuario")
        REFERENCES usuario ("UsuIdUsuario") ON DELETE RESTRICT,
    CONSTRAINT fk_informe_ruta       FOREIGN KEY ("InfIdRuta")
        REFERENCES ruta_peatonal ("RutIdRuta") ON DELETE RESTRICT,
    CONSTRAINT ck_informe_total      CHECK ("InfTotalEstimado" >= 0)
);
 
-- ----------------------------------------------------------------------------
-- 11. CONTROL_AFORO  (RF-16 / RNF-08 concurrencia)
-- ----------------------------------------------------------------------------
CREATE TABLE control_aforo (
    "AfoIdAforo"         INTEGER GENERATED ALWAYS AS IDENTITY,
    "AfoIdZona"          INTEGER   NOT NULL,
    "AfoFecha"           DATE      NOT NULL,
    "AfoCupoUtilizado"   INTEGER   NOT NULL DEFAULT 0,
    CONSTRAINT pk_control_aforo      PRIMARY KEY ("AfoIdAforo"),
    CONSTRAINT fk_aforo_zona         FOREIGN KEY ("AfoIdZona")
        REFERENCES zona_turistica ("ZonIdZona") ON DELETE RESTRICT,
    CONSTRAINT uq_aforo_zona_fecha   UNIQUE ("AfoIdZona", "AfoFecha"),
    CONSTRAINT ck_aforo_cupo         CHECK ("AfoCupoUtilizado" >= 0)
);
 
-- ----------------------------------------------------------------------------
-- 12. AUDITORIA_LOG  (RNF-07 trazabilidad; sin FK fisica)
-- ----------------------------------------------------------------------------
CREATE TABLE auditoria_log (
    "AudIdLog"           INTEGER GENERATED ALWAYS AS IDENTITY,
    "AudFecha"           TIMESTAMP      NOT NULL DEFAULT NOW(),
    "AudUsuario"         VARCHAR(50)    NOT NULL,
    "AudOperacion"       VARCHAR(20)    NOT NULL,
    "AudTablaAfectada"   VARCHAR(50)    NOT NULL,
    "AudValorAnterior"   VARCHAR(500)   NULL,
    "AudValorNuevo"      VARCHAR(500)   NULL,
    CONSTRAINT pk_auditoria_log      PRIMARY KEY ("AudIdLog"),
    CONSTRAINT ck_auditoria_operacion CHECK ("AudOperacion" IN
        ('INSERT', 'UPDATE', 'DELETE', 'SYNC'))
);
 
-- ============================================================================
-- INDICES  (soporte al RNF-01: tiempo de respuesta menor a 2 segundos)
-- ============================================================================
CREATE INDEX idx_zona_estacion      ON zona_turistica    ("ZonIdEstacionCercana");
CREATE INDEX idx_zti_zona           ON zona_tipo_turismo ("ZtiIdZonaTuristica");
CREATE INDEX idx_zti_tipo           ON zona_tipo_turismo ("ZtiIdTipoTurismo");
CREATE INDEX idx_servicio_origen    ON servicio_tren     ("SerIdEstacionOrigen");
CREATE INDEX idx_servicio_destino   ON servicio_tren     ("SerIdEstacionDestino");
CREATE INDEX idx_ruta_estacion      ON ruta_peatonal     ("RutIdEstacionOrigen");
CREATE INDEX idx_ruta_zona          ON ruta_peatonal     ("RutIdZonaDestino");
CREATE INDEX idx_clima_estacion     ON prevision_clima   ("CliIdEstacion");
CREATE INDEX idx_clima_fecha        ON prevision_clima   ("CliFecha");
CREATE INDEX idx_auditoria_fecha    ON auditoria_log     ("AudFecha");
CREATE INDEX idx_aforo_zona_fecha   ON control_aforo     ("AfoIdZona", "AfoFecha");