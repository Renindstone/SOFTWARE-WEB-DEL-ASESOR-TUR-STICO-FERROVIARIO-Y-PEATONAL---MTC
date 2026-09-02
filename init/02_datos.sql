-- ============================================================================
-- Proyecto : Asesor Turistico Ferroviario y Peatonal - MTC
-- Curso    : Ingenieria de Software - UNU
-- Motor    : PostgreSQL 15
-- Script   : 02_datos.sql  (DML - datos de prueba / semilla)
--
-- Ejecutar DESPUES de 01_esquema.sql
--
-- NOTA 1: las llaves primarias son GENERATED ALWAYS AS IDENTITY, por lo que
--         NO se insertan valores de ID explicitos. Las llaves foraneas se
--         resuelven mediante subconsultas sobre los campos UNIQUE de cada
--         tabla, para que el script sea independiente del orden de los IDs.
--
-- NOTA 2: las estaciones y sus coordenadas corresponden a la red real de
--         PeruRail (corredores Cusco-Machu Picchu y Cusco-Puno-Arequipa).
--         Los horarios y tarifas son datos de prueba representativos
--         (simulacion del feed de PeruRail definida en el Sprint 0).
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. ROL
-- ----------------------------------------------------------------------------
INSERT INTO rol ("RolNombreRol") VALUES
    ('ADMIN_MTC'),
    ('TRAVEL_GROUP_USER'),
    ('PERURAIL_ADMIN'),
    ('TURISTA_PUBLICO');

-- ----------------------------------------------------------------------------
-- 2. USUARIO
--
-- Contrasenias en texto plano (solo para pruebas), ya cifradas con BCrypt:
--   admin_mtc    -> Admin1234
--   travel_ana   -> Travel1234
--   rail_luis    -> Rail1234
--   turista_jose -> Turista1234
-- ----------------------------------------------------------------------------
INSERT INTO usuario ("UsuNombreUsuario", "UsuContrasenia", "UsuNombre",
                     "UsuApellidos", "UsuEmail", "UsuIdRol", "UsuEstado") VALUES
    ('admin_mtc',
     '$2b$10$uafTupUNhGEy/Q8LmUrkweIMSgPEXetALfskq0D14GNJeL8OXVx6a',
     'Carlos', 'Torres Ramirez', 'carlos.torres@mtc.gob.pe',
     (SELECT "RolIdRol" FROM rol WHERE "RolNombreRol" = 'ADMIN_MTC'), 'Activo'),
    ('travel_ana',
     '$2b$10$QQzjSUdbVrZIf6V17PO1WOI4ANoTGUG4WeNqiLO8C7j/NT6R.6Ryi',
     'Ana', 'Vargas Quispe', 'ana.vargas@travelgroup.com.pe',
     (SELECT "RolIdRol" FROM rol WHERE "RolNombreRol" = 'TRAVEL_GROUP_USER'), 'Activo'),
    ('rail_luis',
     '$2b$10$IQ.vbYQOzibT51gV2acYre/CyXj6EKjUUyyV8vK/WeZi3rMHg6Cqi',
     'Luis', 'Mamani Huaman', 'luis.mamani@perurail.com',
     (SELECT "RolIdRol" FROM rol WHERE "RolNombreRol" = 'PERURAIL_ADMIN'), 'Activo'),
    ('turista_jose',
     '$2b$10$GiYXMp5scEMkiv5reEIZ1OGqnF2WK.CD1.v1RlHz5BYhC6.pgnWZO',
     'Jose', 'Rojas Diaz', 'jose.rojas@correo.pe',
     (SELECT "RolIdRol" FROM rol WHERE "RolNombreRol" = 'TURISTA_PUBLICO'), 'Activo');

-- ----------------------------------------------------------------------------
-- 3. TIPO_TURISMO  (tabla parametrica - RNF-06)
-- ----------------------------------------------------------------------------
INSERT INTO tipo_turismo ("TipNombre", "TipDescripcion") VALUES
    ('Historia/Cultura', 'Sitios arqueologicos, templos, museos y centros historicos'),
    ('Naturaleza',       'Paisajes, miradores, jardines, aguas termales y flora local'),
    ('Aventura',         'Rutas de ascenso, senderos exigentes y actividades al aire libre'),
    ('Gastronomia',      'Mercados tradicionales y zonas de comida local');

-- ----------------------------------------------------------------------------
-- 4. ESTACION  (red real de PeruRail)
--
-- La estacion Poroy se registra como 'Inactiva': permite validar el RF-02
-- (excluir estaciones inactivas del selector) y el caso de prueba CN-02.
-- ----------------------------------------------------------------------------
INSERT INTO estacion ("EstCodigo", "EstNombre", "EstLatitud", "EstLongitud",
                      "EstAltitud", "EstCiudad", "EstEstado") VALUES
    ('CUS-SPD', 'Estacion San Pedro (Cusco)',            -13.522500, -71.982200, 3399.00, 'Cusco',           'Activa'),
    ('CUS-POR', 'Estacion Poroy',                        -13.474400, -72.042800, 3500.00, 'Cusco',           'Inactiva'),
    ('CUS-URU', 'Estacion Urubamba',                     -13.304900, -72.116300, 2871.00, 'Urubamba',        'Activa'),
    ('CUS-OLL', 'Estacion Ollantaytambo',                -13.258600, -72.265000, 2792.00, 'Ollantaytambo',   'Activa'),
    ('CUS-MAP', 'Estacion Machu Picchu (Aguas Calientes)', -13.154700, -72.525000, 2040.00, 'Aguas Calientes', 'Activa'),
    ('CUS-HID', 'Estacion Hidroelectrica',               -13.174700, -72.547800, 1850.00, 'Santa Teresa',    'Activa'),
    ('PUN-PUN', 'Estacion Puno',                         -15.840200, -70.021900, 3827.00, 'Puno',            'Activa'),
    ('AQP-AQP', 'Estacion Arequipa',                     -16.398900, -71.535000, 2335.00, 'Arequipa',        'Activa');

-- ----------------------------------------------------------------------------
-- 5. SERVICIO_TREN  (horarios y tarifas - simulacion del feed de PeruRail)
-- ----------------------------------------------------------------------------
INSERT INTO servicio_tren ("SerHorarioSalida", "SerHorarioLlegada",
                           "SerTiempoTransitoMin", "SerTarifa",
                           "SerIdEstacionOrigen", "SerIdEstacionDestino") VALUES
    -- Corredor Cusco (San Pedro) <-> Machu Picchu
    ('06:10', '09:54', 224, 210.00,
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-SPD'),
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-MAP')),
    ('14:55', '18:45', 230, 210.00,
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-MAP'),
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-SPD')),
    -- Corredor Ollantaytambo <-> Machu Picchu (servicio Expedition)
    ('05:07', '06:35',  88, 145.00,
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-OLL'),
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-MAP')),
    ('08:53', '10:22',  89, 145.00,
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-MAP'),
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-OLL')),
    -- Corredor Ollantaytambo <-> Machu Picchu (servicio Vistadome)
    ('07:45', '09:05',  80, 195.00,
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-OLL'),
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-MAP')),
    ('15:35', '17:00',  85, 195.00,
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-MAP'),
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-OLL')),
    -- Corredor Urubamba <-> Machu Picchu
    ('06:00', '08:23', 143, 165.00,
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-URU'),
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-MAP')),
    ('16:12', '18:40', 148, 165.00,
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-MAP'),
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-URU')),
    -- Tramo Hidroelectrica <-> Machu Picchu
    ('08:30', '09:10',  40,  90.00,
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-HID'),
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-MAP')),
    ('14:30', '15:10',  40,  90.00,
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-MAP'),
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-HID')),
    -- Corredor Cusco <-> Puno (Titicaca Train)
    ('08:00', '18:00', 600, 950.00,
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-SPD'),
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'PUN-PUN')),
    ('08:00', '18:00', 600, 950.00,
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'PUN-PUN'),
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-SPD')),
    -- Corredor Puno <-> Arequipa
    ('21:00', '08:00', 660, 1200.00,
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'PUN-PUN'),
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'AQP-AQP'));

-- ----------------------------------------------------------------------------
-- 6. ZONA_TURISTICA  (carga de Travel Group Peru)
--
-- Solo zonas alcanzables A PIE desde la estacion asociada (modelo del caso).
-- "ZonCupoMaximoDiario" se completa unicamente en las zonas que manejan
-- aforo controlado; en el resto queda NULL (RF-16).
-- ----------------------------------------------------------------------------
INSERT INTO zona_turistica ("ZonNombre", "ZonDescripcion", "ZonIdEstacionCercana",
                            "ZonCostoAprox", "ZonCupoMaximoDiario", "ZonEstado") VALUES
    -- Desde Estacion San Pedro (Cusco)
    ('Mercado Central de San Pedro',
     'Mercado tradicional cusqueno con puestos de comida, jugos y artesania local.',
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-SPD'), 0.00, NULL, 'Activa'),
    ('Plaza de Armas del Cusco',
     'Centro historico de la ciudad, rodeado por la Catedral y la Iglesia de la Compania.',
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-SPD'), 0.00, NULL, 'Activa'),
    ('Templo del Qorikancha',
     'Antiguo templo inca dedicado al Sol, sobre el cual se edifico el convento de Santo Domingo.',
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-SPD'), 15.00, NULL, 'Activa'),
    ('Barrio de San Blas',
     'Barrio de artesanos con calles empedradas, miradores y talleres tradicionales.',
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-SPD'), 0.00, NULL, 'Activa'),
    ('Parque Arqueologico de Sacsayhuaman',
     'Complejo ceremonial inca de muros ciclopeos, con vista panoramica de la ciudad del Cusco.',
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-SPD'), 70.00, 3000, 'Activa'),
    -- Desde Estacion Ollantaytambo
    ('Conjunto Arqueologico de Ollantaytambo',
     'Fortaleza y centro ceremonial inca con andenerias y el Templo del Sol.',
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-OLL'), 70.00, 2500, 'Activa'),
    ('Colcas de Pinkuylluna',
     'Antiguos depositos incas ubicados en la ladera del cerro, con vista al valle.',
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-OLL'), 0.00, NULL, 'Activa'),
    -- Desde Estacion Machu Picchu (Aguas Calientes)
    ('Llaqta de Machu Picchu',
     'Santuario historico inca declarado Patrimonio de la Humanidad por la UNESCO.',
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-MAP'), 152.00, 4500, 'Activa'),
    ('Banos Termales de Aguas Calientes',
     'Pozas de aguas termales naturales en el centro del pueblo de Machu Picchu.',
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-MAP'), 20.00, NULL, 'Activa'),
    ('Jardines de Mandor',
     'Reserva privada con senderos, catarata y avistamiento de aves y orquideas.',
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-MAP'), 10.00, NULL, 'Activa'),
    ('Museo de Sitio Manuel Chavez Ballon',
     'Museo con piezas y hallazgos de las excavaciones del santuario de Machu Picchu.',
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-MAP'), 22.00, NULL, 'Activa'),
    -- Desde Estacion Urubamba
    ('Plaza de Armas de Urubamba',
     'Plaza principal del valle sagrado, con iglesia colonial y mercado artesanal.',
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-URU'), 0.00, NULL, 'Activa'),
    -- Desde Estacion Puno
    ('Mirador Kuntur Wasi',
     'Mirador en lo alto de la ciudad con vista panoramica del lago Titicaca.',
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'PUN-PUN'), 5.00, NULL, 'Activa'),
    ('Puerto Lacustre de Puno',
     'Malecon y muelle turistico a orillas del lago Titicaca.',
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'PUN-PUN'), 0.00, NULL, 'Activa'),
    -- Desde Estacion Arequipa
    ('Monasterio de Santa Catalina',
     'Ciudadela religiosa colonial con calles, patios y arquitectura en sillar.',
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'AQP-AQP'), 45.00, NULL, 'Activa'),
    ('Mirador de Yanahuara',
     'Mirador de arcos de sillar con vista al volcan Misti.',
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'AQP-AQP'), 0.00, NULL, 'Activa'),
    -- Zona dada de baja (permite validar el filtrado por estado)
    ('Sendero Antiguo del Rio Vilcanota',
     'Sendero cerrado temporalmente por trabajos de mantenimiento en la ribera.',
     (SELECT "EstIdEstacion" FROM estacion WHERE "EstCodigo" = 'CUS-OLL'), 0.00, NULL, 'Inactiva');

-- ----------------------------------------------------------------------------
-- 7. ZONA_TIPO_TURISMO  (relacion N:M)
--
-- Varias zonas combinan mas de una categoria a la vez: por ejemplo, el
-- Conjunto Arqueologico de Ollantaytambo es Historia/Cultura Y Naturaleza,
-- por lo que debe aparecer en las busquedas de ambas preferencias (CN-10).
-- ----------------------------------------------------------------------------
INSERT INTO zona_tipo_turismo ("ZtiIdZonaTuristica", "ZtiIdTipoTurismo")
SELECT z."ZonIdZona", t."TipIdTipoTurismo"
FROM (VALUES
    ('Mercado Central de San Pedro',            'Gastronomia'),
    ('Mercado Central de San Pedro',            'Historia/Cultura'),
    ('Plaza de Armas del Cusco',                'Historia/Cultura'),
    ('Templo del Qorikancha',                   'Historia/Cultura'),
    ('Barrio de San Blas',                      'Historia/Cultura'),
    ('Barrio de San Blas',                      'Gastronomia'),
    ('Parque Arqueologico de Sacsayhuaman',     'Historia/Cultura'),
    ('Parque Arqueologico de Sacsayhuaman',     'Naturaleza'),
    ('Parque Arqueologico de Sacsayhuaman',     'Aventura'),
    ('Conjunto Arqueologico de Ollantaytambo',  'Historia/Cultura'),
    ('Conjunto Arqueologico de Ollantaytambo',  'Naturaleza'),
    ('Colcas de Pinkuylluna',                   'Historia/Cultura'),
    ('Colcas de Pinkuylluna',                   'Aventura'),
    ('Llaqta de Machu Picchu',                  'Historia/Cultura'),
    ('Llaqta de Machu Picchu',                  'Naturaleza'),
    ('Banos Termales de Aguas Calientes',       'Naturaleza'),
    ('Jardines de Mandor',                      'Naturaleza'),
    ('Jardines de Mandor',                      'Aventura'),
    ('Museo de Sitio Manuel Chavez Ballon',     'Historia/Cultura'),
    ('Plaza de Armas de Urubamba',              'Historia/Cultura'),
    ('Mirador Kuntur Wasi',                     'Naturaleza'),
    ('Mirador Kuntur Wasi',                     'Aventura'),
    ('Puerto Lacustre de Puno',                 'Naturaleza'),
    ('Monasterio de Santa Catalina',            'Historia/Cultura'),
    ('Mirador de Yanahuara',                    'Historia/Cultura'),
    ('Mirador de Yanahuara',                    'Naturaleza'),
    ('Sendero Antiguo del Rio Vilcanota',       'Naturaleza')
) AS v(zona, tipo)
JOIN zona_turistica z ON z."ZonNombre"  = v.zona
JOIN tipo_turismo   t ON t."TipNombre"  = v.tipo;

-- ----------------------------------------------------------------------------
-- 8. RUTA_PEATONAL  (circuitos de ida y vuelta - RNF-04)
--
-- "RutDistanciaKm" corresponde al recorrido TOTAL (ida + vuelta), es decir,
-- el doble de la distancia estacion -> zona, segun el calculo definido en
-- RutaPeatonalService (formula de Haversine x 2).
-- ----------------------------------------------------------------------------
INSERT INTO ruta_peatonal ("RutNombre", "RutDistanciaKm", "RutTiempoEstimadoMin",
                           "RutDificultad", "RutIdEstacionOrigen",
                           "RutIdZonaDestino", "RutEsIdaVuelta")
SELECT v.nombre, v.km, v.minutos, v.dificultad,
       z."ZonIdEstacionCercana", z."ZonIdZona", TRUE
FROM (VALUES
    ('Circuito San Pedro - Mercado Central',        0.30,   8, 'Baja',  'Mercado Central de San Pedro'),
    ('Circuito San Pedro - Plaza de Armas',         1.20,  25, 'Baja',  'Plaza de Armas del Cusco'),
    ('Circuito San Pedro - Qorikancha',             1.80,  35, 'Baja',  'Templo del Qorikancha'),
    ('Circuito San Pedro - San Blas',               2.40,  55, 'Media', 'Barrio de San Blas'),
    ('Circuito San Pedro - Sacsayhuaman',           4.60, 130, 'Alta',  'Parque Arqueologico de Sacsayhuaman'),
    ('Circuito Ollantaytambo - Fortaleza',          1.20,  30, 'Baja',  'Conjunto Arqueologico de Ollantaytambo'),
    ('Circuito Ollantaytambo - Pinkuylluna',        1.80,  75, 'Alta',  'Colcas de Pinkuylluna'),
    ('Circuito Aguas Calientes - Llaqta a pie',    10.00, 240, 'Alta',  'Llaqta de Machu Picchu'),
    ('Circuito Aguas Calientes - Banos Termales',   1.60,  35, 'Baja',  'Banos Termales de Aguas Calientes'),
    ('Circuito Aguas Calientes - Mandor',           6.40, 130, 'Media', 'Jardines de Mandor'),
    ('Circuito Aguas Calientes - Museo de Sitio',   3.80,  90, 'Media', 'Museo de Sitio Manuel Chavez Ballon'),
    ('Circuito Urubamba - Plaza de Armas',          2.20,  40, 'Baja',  'Plaza de Armas de Urubamba'),
    ('Circuito Puno - Mirador Kuntur Wasi',         2.80,  70, 'Alta',  'Mirador Kuntur Wasi'),
    ('Circuito Puno - Puerto Lacustre',             2.00,  30, 'Baja',  'Puerto Lacustre de Puno'),
    ('Circuito Arequipa - Santa Catalina',          2.60,  45, 'Baja',  'Monasterio de Santa Catalina'),
    ('Circuito Arequipa - Mirador de Yanahuara',    4.20,  80, 'Media', 'Mirador de Yanahuara')
) AS v(nombre, km, minutos, dificultad, zona)
JOIN zona_turistica z ON z."ZonNombre" = v.zona;

-- ----------------------------------------------------------------------------
-- 9. PREVISION_CLIMA  (simulacion del feed diario del SENAMHI)
-- ----------------------------------------------------------------------------
INSERT INTO prevision_clima ("CliFecha", "CliTemperaturaC", "CliProbabilidadLluvia",
                             "CliEstadoClima", "CliIdEstacion")
SELECT v.fecha::DATE, v.temp, v.lluvia, v.estado,
       e."EstIdEstacion"
FROM (VALUES
    ('2026-09-01', 18.5, 10.0, 'Soleado',            'CUS-SPD'),
    ('2026-09-02', 17.2, 25.0, 'Parcialmente nublado','CUS-SPD'),
    ('2026-09-03', 16.8, 40.0, 'Nublado',            'CUS-SPD'),
    ('2026-09-01', 20.1, 15.0, 'Soleado',            'CUS-OLL'),
    ('2026-09-02', 19.4, 30.0, 'Parcialmente nublado','CUS-OLL'),
    ('2026-09-03', 18.0, 55.0, 'Lluvia ligera',      'CUS-OLL'),
    ('2026-09-01', 23.6, 35.0, 'Parcialmente nublado','CUS-MAP'),
    ('2026-09-02', 22.9, 60.0, 'Lluvia ligera',      'CUS-MAP'),
    ('2026-09-03', 21.5, 80.0, 'Lluvioso',           'CUS-MAP'),
    ('2026-09-01', 21.0, 20.0, 'Soleado',            'CUS-URU'),
    ('2026-09-02', 20.3, 35.0, 'Parcialmente nublado','CUS-URU'),
    ('2026-09-01', 14.2, 12.0, 'Soleado',            'PUN-PUN'),
    ('2026-09-02', 13.8, 22.0, 'Parcialmente nublado','PUN-PUN'),
    ('2026-09-01', 22.4,  5.0, 'Soleado',            'AQP-AQP'),
    ('2026-09-02', 22.0,  8.0, 'Soleado',            'AQP-AQP')
) AS v(fecha, temp, lluvia, estado, codigo)
JOIN estacion e ON e."EstCodigo" = v.codigo;

-- ----------------------------------------------------------------------------
-- 10. CONTROL_AFORO  (RF-16)
--
-- La Llaqta de Machu Picchu queda con el aforo COMPLETO para el 01/09/2026
-- (4500 de 4500), lo que permite ejecutar directamente el caso de prueba
-- CN-09 (rechazo de informe por aforo agotado).
-- ----------------------------------------------------------------------------
INSERT INTO control_aforo ("AfoIdZona", "AfoFecha", "AfoCupoUtilizado")
SELECT z."ZonIdZona", v.fecha::DATE, v.cupo
FROM (VALUES
    ('Llaqta de Machu Picchu',                 '2026-09-01', 4500),
    ('Llaqta de Machu Picchu',                 '2026-09-02', 1820),
    ('Llaqta de Machu Picchu',                 '2026-09-03',  640),
    ('Conjunto Arqueologico de Ollantaytambo', '2026-09-01',  320),
    ('Conjunto Arqueologico de Ollantaytambo', '2026-09-02',  145),
    ('Parque Arqueologico de Sacsayhuaman',    '2026-09-01',  980)
) AS v(zona, fecha, cupo)
JOIN zona_turistica z ON z."ZonNombre" = v.zona;

-- ----------------------------------------------------------------------------
-- 11. INFORME_PLANIFICACION  (RF-08)
--
-- InfTotalEstimado = tarifa del tren + costo aproximado de la zona.
-- El informe INF-0003 se genera sin usuario autenticado (consulta anonima).
-- ----------------------------------------------------------------------------
INSERT INTO informe_planificacion ("InfCodigo", "InfFechaVisita", "InfIdUsuario",
                                   "InfIdRuta", "InfTotalEstimado") VALUES
    ('INF-0001', '2026-09-02',
     (SELECT "UsuIdUsuario" FROM usuario WHERE "UsuNombreUsuario" = 'turista_jose'),
     (SELECT "RutIdRuta" FROM ruta_peatonal WHERE "RutNombre" = 'Circuito Ollantaytambo - Fortaleza'),
     215.00),
    ('INF-0002', '2026-09-03',
     (SELECT "UsuIdUsuario" FROM usuario WHERE "UsuNombreUsuario" = 'turista_jose'),
     (SELECT "RutIdRuta" FROM ruta_peatonal WHERE "RutNombre" = 'Circuito Aguas Calientes - Banos Termales'),
     165.00),
    ('INF-0003', '2026-09-02', NULL,
     (SELECT "RutIdRuta" FROM ruta_peatonal WHERE "RutNombre" = 'Circuito San Pedro - Sacsayhuaman'),
     70.00);

-- ----------------------------------------------------------------------------
-- 12. AUDITORIA_LOG  (RNF-07)
-- ----------------------------------------------------------------------------
INSERT INTO auditoria_log ("AudUsuario", "AudOperacion", "AudTablaAfectada",
                           "AudValorAnterior", "AudValorNuevo") VALUES
    ('SISTEMA',    'SYNC',   'estacion',
     NULL, 'Sincronizacion PeruRail: 8 estaciones actualizadas'),
    ('SISTEMA',    'SYNC',   'servicio_tren',
     NULL, 'Sincronizacion PeruRail: 13 servicios actualizados'),
    ('SISTEMA',    'SYNC',   'prevision_clima',
     NULL, 'Sincronizacion SENAMHI: 15 previsiones actualizadas'),
    ('travel_ana', 'INSERT', 'zona_turistica',
     NULL, 'ZonNombre=Jardines de Mandor; ZonIdEstacionCercana=CUS-MAP'),
    ('travel_ana', 'UPDATE', 'zona_turistica',
     'ZonCostoAprox=8.00', 'ZonCostoAprox=10.00'),
    ('rail_luis',  'UPDATE', 'servicio_tren',
     'SerTarifa=140.00', 'SerTarifa=145.00'),
    ('admin_mtc',  'UPDATE', 'estacion',
     'EstEstado=Activa', 'EstEstado=Inactiva (Estacion Poroy)');

-- ============================================================================
-- FIN DEL SCRIPT DE DATOS DE PRUEBA
-- ============================================================================
