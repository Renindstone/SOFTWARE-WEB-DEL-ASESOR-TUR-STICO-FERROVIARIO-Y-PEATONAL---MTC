-- ============================================================================
-- Proyecto : Asesor Turistico Ferroviario y Peatonal - MTC
-- Curso    : Ingenieria de Software - UNU
-- Motor    : PostgreSQL 15
-- Script   : 03_consultas.sql  (VISTAS y FUNCIONES de apoyo)
--
-- Ejecutar DESPUES de 01_esquema.sql y 02_datos.sql
--
-- Objetivo: facilitar la revision de los datos desde DBeaver sin escribir
-- JOINs manualmente cada vez, y encapsular la logica de negocio que luego
-- consumen los servicios de Spring Boot (RutaPeatonalService, AforoService).
--
-- Uso rapido en DBeaver:
--   SELECT * FROM vw_zonas_turisticas;
--   SELECT * FROM fn_buscar_zonas('CUS-OLL', 'Naturaleza');
--   SELECT * FROM fn_verificar_aforo('Llaqta de Machu Picchu', '2026-09-01');
--   SELECT * FROM fn_resumen_bd();
-- ============================================================================


-- ============================================================================
-- PARTE 1: VISTAS  (consulta directa, solo lectura)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- vw_zonas_turisticas
-- Zona turistica con su estacion, sus tipos de turismo agrupados y su ruta.
-- Es la vista mas util para revisar de un vistazo el catalogo completo.
-- ----------------------------------------------------------------------------
CREATE OR REPLACE VIEW vw_zonas_turisticas AS
SELECT
    z."ZonIdZona"                                       AS id_zona,
    z."ZonNombre"                                       AS zona,
    e."EstNombre"                                       AS estacion,
    e."EstCiudad"                                       AS ciudad,
    string_agg(DISTINCT t."TipNombre", ' + '
               ORDER BY t."TipNombre")                  AS tipos_turismo,
    r."RutDistanciaKm"                                  AS km_ida_vuelta,
    r."RutTiempoEstimadoMin"                            AS minutos,
    r."RutDificultad"                                   AS dificultad,
    z."ZonCostoAprox"                                   AS costo_zona,
    z."ZonCupoMaximoDiario"                             AS cupo_diario,
    z."ZonEstado"                                       AS estado
FROM zona_turistica z
JOIN estacion e            ON e."EstIdEstacion"    = z."ZonIdEstacionCercana"
LEFT JOIN zona_tipo_turismo zt ON zt."ZtiIdZonaTuristica" = z."ZonIdZona"
LEFT JOIN tipo_turismo t   ON t."TipIdTipoTurismo" = zt."ZtiIdTipoTurismo"
LEFT JOIN ruta_peatonal r  ON r."RutIdZonaDestino" = z."ZonIdZona"
GROUP BY z."ZonIdZona", z."ZonNombre", e."EstNombre", e."EstCiudad",
         r."RutDistanciaKm", r."RutTiempoEstimadoMin", r."RutDificultad",
         z."ZonCostoAprox", z."ZonCupoMaximoDiario", z."ZonEstado";


-- ----------------------------------------------------------------------------
-- vw_servicios_tren
-- Horarios y tarifas de PeruRail con los nombres de estacion resueltos.
-- ----------------------------------------------------------------------------
CREATE OR REPLACE VIEW vw_servicios_tren AS
SELECT
    s."SerIdServicio"          AS id_servicio,
    o."EstNombre"              AS origen,
    d."EstNombre"              AS destino,
    s."SerHorarioSalida"       AS salida,
    s."SerHorarioLlegada"      AS llegada,
    s."SerTiempoTransitoMin"   AS minutos,
    s."SerTarifa"              AS tarifa_soles
FROM servicio_tren s
JOIN estacion o ON o."EstIdEstacion" = s."SerIdEstacionOrigen"
JOIN estacion d ON d."EstIdEstacion" = s."SerIdEstacionDestino";


-- ----------------------------------------------------------------------------
-- vw_clima
-- Pronostico del SENAMHI por estacion y fecha.
-- ----------------------------------------------------------------------------
CREATE OR REPLACE VIEW vw_clima AS
SELECT
    c."CliFecha"               AS fecha,
    e."EstNombre"              AS estacion,
    e."EstCiudad"              AS ciudad,
    c."CliTemperaturaC"        AS temperatura_c,
    c."CliProbabilidadLluvia"  AS prob_lluvia_pct,
    c."CliEstadoClima"         AS estado_clima
FROM prevision_clima c
JOIN estacion e ON e."EstIdEstacion" = c."CliIdEstacion";


-- ----------------------------------------------------------------------------
-- vw_aforo
-- Estado del cupo diario por zona y fecha (RF-16).
-- ----------------------------------------------------------------------------
CREATE OR REPLACE VIEW vw_aforo AS
SELECT
    z."ZonNombre"                                AS zona,
    a."AfoFecha"                                 AS fecha,
    a."AfoCupoUtilizado"                         AS usado,
    z."ZonCupoMaximoDiario"                      AS maximo,
    z."ZonCupoMaximoDiario" - a."AfoCupoUtilizado" AS disponible,
    ROUND(100.0 * a."AfoCupoUtilizado"
          / NULLIF(z."ZonCupoMaximoDiario", 0), 1) AS ocupacion_pct,
    CASE WHEN a."AfoCupoUtilizado" >= z."ZonCupoMaximoDiario"
         THEN 'COMPLETO' ELSE 'DISPONIBLE' END   AS estado
FROM control_aforo a
JOIN zona_turistica z ON z."ZonIdZona" = a."AfoIdZona";


-- ----------------------------------------------------------------------------
-- vw_informes
-- Informes generados con su ruta, zona y usuario (NULL = consulta anonima).
-- ----------------------------------------------------------------------------
CREATE OR REPLACE VIEW vw_informes AS
SELECT
    i."InfCodigo"                              AS codigo,
    i."InfFechaEmision"                        AS emitido,
    i."InfFechaVisita"                         AS fecha_visita,
    COALESCE(u."UsuNombreUsuario", '(anonimo)') AS usuario,
    r."RutNombre"                              AS ruta,
    z."ZonNombre"                              AS zona,
    e."EstNombre"                              AS estacion_origen,
    i."InfTotalEstimado"                       AS total_soles
FROM informe_planificacion i
JOIN ruta_peatonal r   ON r."RutIdRuta"      = i."InfIdRuta"
JOIN zona_turistica z  ON z."ZonIdZona"      = r."RutIdZonaDestino"
JOIN estacion e        ON e."EstIdEstacion"  = r."RutIdEstacionOrigen"
LEFT JOIN usuario u    ON u."UsuIdUsuario"   = i."InfIdUsuario";


-- ----------------------------------------------------------------------------
-- vw_auditoria
-- Log de trazabilidad ordenado del mas reciente al mas antiguo (RNF-07).
-- ----------------------------------------------------------------------------
CREATE OR REPLACE VIEW vw_auditoria AS
SELECT
    "AudFecha"          AS fecha,
    "AudUsuario"        AS usuario,
    "AudOperacion"      AS operacion,
    "AudTablaAfectada"  AS tabla,
    "AudValorAnterior"  AS valor_anterior,
    "AudValorNuevo"     AS valor_nuevo
FROM auditoria_log
ORDER BY "AudFecha" DESC, "AudIdLog" DESC;


-- ============================================================================
-- PARTE 2: FUNCIONES
-- ============================================================================

-- ----------------------------------------------------------------------------
-- fn_distancia_haversine(lat1, lon1, lat2, lon2)
-- Distancia en km en linea recta entre dos coordenadas.
-- Es la formula que implementa RutaPeatonalService (seccion 5.1 del documento).
-- ----------------------------------------------------------------------------
-- NOTA: las funciones trigonometricas de PostgreSQL devuelven DOUBLE PRECISION,
-- por lo que el resultado se convierte a NUMERIC antes de aplicar ROUND(x, 2).
CREATE OR REPLACE FUNCTION fn_distancia_haversine(
    lat1 NUMERIC, lon1 NUMERIC,
    lat2 NUMERIC, lon2 NUMERIC
) RETURNS NUMERIC AS $$
DECLARE
    radio_tierra CONSTANT DOUBLE PRECISION := 6371;  -- km
    d_lat  DOUBLE PRECISION;
    d_lon  DOUBLE PRECISION;
    a      DOUBLE PRECISION;
BEGIN
    d_lat := RADIANS(lat2 - lat1);
    d_lon := RADIANS(lon2 - lon1);
    a := SIN(d_lat / 2) ^ 2
         + COS(RADIANS(lat1)) * COS(RADIANS(lat2)) * SIN(d_lon / 2) ^ 2;
    RETURN ROUND((radio_tierra * 2 * ATAN2(SQRT(a), SQRT(1 - a)))::NUMERIC, 2);
END;
$$ LANGUAGE plpgsql IMMUTABLE;


-- ----------------------------------------------------------------------------
-- fn_buscar_zonas(codigo_estacion, tipo_turismo, dificultad)
-- Busqueda principal del turista (RF-03).
--   - p_tipo y p_dificultad son opcionales: pasar NULL para no filtrar.
--   - Excluye estaciones y zonas inactivas (RF-02).
-- Ejemplos:
--   SELECT * FROM fn_buscar_zonas('CUS-OLL', 'Naturaleza', NULL);
--   SELECT * FROM fn_buscar_zonas('CUS-SPD', NULL, 'Baja');
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_buscar_zonas(
    p_codigo_estacion VARCHAR,
    p_tipo            VARCHAR DEFAULT NULL,
    p_dificultad      VARCHAR DEFAULT NULL
) RETURNS TABLE (
    zona           VARCHAR,
    tipos_turismo  TEXT,
    km_ida_vuelta  NUMERIC,
    minutos        INTEGER,
    dificultad     VARCHAR,
    costo_zona     NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    SELECT z."ZonNombre",
           string_agg(DISTINCT t2."TipNombre", ' + ' ORDER BY t2."TipNombre"),
           r."RutDistanciaKm",
           r."RutTiempoEstimadoMin",
           r."RutDificultad",
           z."ZonCostoAprox"
    FROM zona_turistica z
    JOIN estacion e           ON e."EstIdEstacion"      = z."ZonIdEstacionCercana"
    JOIN ruta_peatonal r      ON r."RutIdZonaDestino"   = z."ZonIdZona"
    JOIN zona_tipo_turismo zt ON zt."ZtiIdZonaTuristica" = z."ZonIdZona"
    JOIN tipo_turismo t2      ON t2."TipIdTipoTurismo"  = zt."ZtiIdTipoTurismo"
    WHERE e."EstCodigo" = p_codigo_estacion
      AND e."EstEstado" = 'Activa'
      AND z."ZonEstado" = 'Activa'
      AND (p_dificultad IS NULL OR r."RutDificultad" = p_dificultad)
      AND (p_tipo IS NULL OR EXISTS (
            SELECT 1
            FROM zona_tipo_turismo zt2
            JOIN tipo_turismo t3 ON t3."TipIdTipoTurismo" = zt2."ZtiIdTipoTurismo"
            WHERE zt2."ZtiIdZonaTuristica" = z."ZonIdZona"
              AND t3."TipNombre" = p_tipo))
    GROUP BY z."ZonNombre", r."RutDistanciaKm", r."RutTiempoEstimadoMin",
             r."RutDificultad", z."ZonCostoAprox"
    ORDER BY r."RutDistanciaKm";
END;
$$ LANGUAGE plpgsql;


-- ----------------------------------------------------------------------------
-- fn_verificar_aforo(nombre_zona, fecha)
-- Consulta el estado del cupo diario de una zona (RF-16, apoyo a CN-09).
-- Devuelve 'SIN CONTROL' si la zona no maneja aforo.
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_verificar_aforo(
    p_zona  VARCHAR,
    p_fecha DATE
) RETURNS TABLE (
    zona       VARCHAR,
    fecha      DATE,
    usado      INTEGER,
    maximo     INTEGER,
    disponible INTEGER,
    estado     TEXT
) AS $$
BEGIN
    RETURN QUERY
    SELECT z."ZonNombre",
           p_fecha,
           COALESCE(a."AfoCupoUtilizado", 0),
           z."ZonCupoMaximoDiario",
           z."ZonCupoMaximoDiario" - COALESCE(a."AfoCupoUtilizado", 0),
           CASE
               WHEN z."ZonCupoMaximoDiario" IS NULL THEN 'SIN CONTROL'
               WHEN COALESCE(a."AfoCupoUtilizado", 0) >= z."ZonCupoMaximoDiario"
                    THEN 'COMPLETO'
               ELSE 'DISPONIBLE'
           END
    FROM zona_turistica z
    LEFT JOIN control_aforo a
           ON a."AfoIdZona" = z."ZonIdZona"
          AND a."AfoFecha"  = p_fecha
    WHERE z."ZonNombre" = p_zona;
END;
$$ LANGUAGE plpgsql;


-- ----------------------------------------------------------------------------
-- fn_itinerario(codigo_estacion, fecha)
-- Arma el "informe consolidado" de una estacion: zonas alcanzables a pie,
-- clima del dia, aforo y costo total estimado (RF-08).
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_itinerario(
    p_codigo_estacion VARCHAR,
    p_fecha           DATE
) RETURNS TABLE (
    zona           VARCHAR,
    km_ida_vuelta  NUMERIC,
    minutos        INTEGER,
    dificultad     VARCHAR,
    clima          VARCHAR,
    temperatura_c  NUMERIC,
    aforo          TEXT,
    costo_total    NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    SELECT z."ZonNombre",
           r."RutDistanciaKm",
           r."RutTiempoEstimadoMin",
           r."RutDificultad",
           c."CliEstadoClima",
           c."CliTemperaturaC",
           CASE
               WHEN z."ZonCupoMaximoDiario" IS NULL THEN 'SIN CONTROL'
               WHEN COALESCE(a."AfoCupoUtilizado", 0) >= z."ZonCupoMaximoDiario"
                    THEN 'COMPLETO'
               ELSE 'DISPONIBLE'
           END,
           COALESCE(z."ZonCostoAprox", 0)
           + COALESCE((SELECT MIN(s."SerTarifa")
                       FROM servicio_tren s
                       WHERE s."SerIdEstacionDestino" = e."EstIdEstacion"), 0)
    FROM zona_turistica z
    JOIN estacion e          ON e."EstIdEstacion"    = z."ZonIdEstacionCercana"
    JOIN ruta_peatonal r     ON r."RutIdZonaDestino" = z."ZonIdZona"
    LEFT JOIN prevision_clima c
           ON c."CliIdEstacion" = e."EstIdEstacion" AND c."CliFecha" = p_fecha
    LEFT JOIN control_aforo a
           ON a."AfoIdZona" = z."ZonIdZona" AND a."AfoFecha" = p_fecha
    WHERE e."EstCodigo" = p_codigo_estacion
      AND e."EstEstado" = 'Activa'
      AND z."ZonEstado" = 'Activa'
    ORDER BY r."RutDistanciaKm";
END;
$$ LANGUAGE plpgsql;


-- ----------------------------------------------------------------------------
-- fn_resumen_bd()
-- Conteo de registros por tabla. Util para verificar que la carga de datos
-- se ejecuto completa despues de levantar el contenedor.
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION fn_resumen_bd()
RETURNS TABLE (tabla TEXT, registros BIGINT) AS $$
BEGIN
    RETURN QUERY
    SELECT 'rol'::TEXT,                   COUNT(*) FROM rol
    UNION ALL SELECT 'usuario',           COUNT(*) FROM usuario
    UNION ALL SELECT 'tipo_turismo',      COUNT(*) FROM tipo_turismo
    UNION ALL SELECT 'estacion',          COUNT(*) FROM estacion
    UNION ALL SELECT 'servicio_tren',     COUNT(*) FROM servicio_tren
    UNION ALL SELECT 'zona_turistica',    COUNT(*) FROM zona_turistica
    UNION ALL SELECT 'zona_tipo_turismo', COUNT(*) FROM zona_tipo_turismo
    UNION ALL SELECT 'ruta_peatonal',     COUNT(*) FROM ruta_peatonal
    UNION ALL SELECT 'prevision_clima',   COUNT(*) FROM prevision_clima
    UNION ALL SELECT 'informe_planificacion', COUNT(*) FROM informe_planificacion
    UNION ALL SELECT 'control_aforo',     COUNT(*) FROM control_aforo
    UNION ALL SELECT 'auditoria_log',     COUNT(*) FROM auditoria_log;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- FIN DEL SCRIPT DE VISTAS Y FUNCIONES
-- ============================================================================
