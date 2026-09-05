package com.turismo.integration.senamhi.mock;

import com.turismo.integration.senamhi.dto.PrevisionClimaSenamhiDTO;
import com.turismo.model.Estacion;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Random;

/**
 * Genera el pronostico que publica el mock del SENAMHI (Sprint 0 del
 * documento: "implementacion de mocks de los servicios externos ... para
 * permitir un desarrollo desacoplado mientras se gestionan los accesos
 * reales").
 *
 * El pronostico es DETERMINISTA: la semilla sale del par (codigo de estacion,
 * fecha), de modo que una misma fecha siempre devuelve los mismos valores.
 * Eso importa por dos razones: la sincronizacion diaria puede repetirse sin
 * que el clima "salte" entre ejecuciones, y las pruebas pueden afirmar
 * valores concretos.
 *
 * El modelo climatico es una aproximacion a la sierra sur peruana, suficiente
 * para que los datos de la demo sean crebles:
 *   - La temperatura baja con la altitud de la estacion.
 *   - Hay dos temporadas marcadas: lluvias de noviembre a marzo y seca de
 *     mayo a septiembre. En la seca llueve mucho menos y la amplitud termica
 *     entre el dia y la noche es bastante mayor.
 */
@Component
public class GeneradorPronosticoSenamhi {

    /** Temperatura maxima aproximada a nivel del mar, en grados Celsius. */
    private static final double TEMP_MAXIMA_NIVEL_MAR = 30.0;

    /** Metros de altitud que hacen bajar la temperatura maxima un grado. */
    private static final double METROS_POR_GRADO = 280.0;

    /** Altitud supuesta cuando la estacion no la tiene registrada (EstAltitud es opcional). */
    private static final double ALTITUD_POR_DEFECTO = 3000.0;

    /** Probabilidad de lluvia en el punto medio del ano, en porcentaje. */
    private static final double LLUVIA_MEDIA = 45.0;

    /** Cuanto se aparta la lluvia de su media entre la temporada humeda y la seca. */
    private static final double LLUVIA_AMPLITUD_ESTACIONAL = 35.0;

    /** Dia del ano mas lluvioso (mediados de enero, pleno periodo de lluvias). */
    private static final int DIA_MAS_LLUVIOSO = 15;

    private static final double AMPLITUD_TERMICA_MEDIA = 9.0;
    private static final double AMPLITUD_TERMICA_ESTACIONAL = 4.0;

    private static final double RUIDO_TEMPERATURA_C = 1.5;
    private static final double RUIDO_LLUVIA_PUNTOS = 8.0;

    /**
     * Pronostico de una estacion para una fecha. La estacion aporta su codigo
     * (identificador del feed) y su altitud.
     */
    public PrevisionClimaSenamhiDTO generar(Estacion estacion, LocalDate fecha) {
        double altitud = estacion.getAltitud() == null
                ? ALTITUD_POR_DEFECTO
                : estacion.getAltitud().doubleValue();

        Random aleatorio = new Random(Objects.hash(estacion.getCodigo(), fecha));

        // +1 en el pico de lluvias, -1 en plena temporada seca.
        double estacionalidad = Math.cos(2 * Math.PI * (fecha.getDayOfYear() - DIA_MAS_LLUVIOSO) / 365.0);

        double probabilidadLluvia = LLUVIA_MEDIA
                + LLUVIA_AMPLITUD_ESTACIONAL * estacionalidad
                + ruido(aleatorio, RUIDO_LLUVIA_PUNTOS);
        probabilidadLluvia = acotar(probabilidadLluvia, 0, 100);

        double temperaturaMaxima = TEMP_MAXIMA_NIVEL_MAR
                - altitud / METROS_POR_GRADO
                - 2 * estacionalidad
                + ruido(aleatorio, RUIDO_TEMPERATURA_C);

        // En la temporada seca las noches son mucho mas frias que en la humeda.
        double amplitudTermica = AMPLITUD_TERMICA_MEDIA - AMPLITUD_TERMICA_ESTACIONAL * estacionalidad;
        double temperaturaMinima = temperaturaMaxima - amplitudTermica;

        PrevisionClimaSenamhiDTO prevision = new PrevisionClimaSenamhiDTO();
        prevision.setCodigoEstacion(estacion.getCodigo());
        prevision.setFecha(fecha.toString());
        prevision.setTemperaturaMinimaC(unDecimal(temperaturaMinima));
        prevision.setTemperaturaMaximaC(unDecimal(temperaturaMaxima));
        prevision.setProbabilidadLluvia(unDecimal(probabilidadLluvia));
        prevision.setEstadoClima(describirEstado(probabilidadLluvia));
        return prevision;
    }

    /**
     * Los estados coinciden con los que ya usan los datos semilla y caben en
     * CliEstadoClima, VARCHAR(30) del diccionario de datos.
     */
    private String describirEstado(double probabilidadLluvia) {
        if (probabilidadLluvia < 20) {
            return "Soleado";
        }
        if (probabilidadLluvia < 40) {
            return "Parcialmente nublado";
        }
        if (probabilidadLluvia < 60) {
            return "Nublado";
        }
        if (probabilidadLluvia < 80) {
            return "Lluvia ligera";
        }
        return "Lluvioso";
    }

    private double ruido(Random aleatorio, double amplitud) {
        return (aleatorio.nextDouble() * 2 - 1) * amplitud;
    }

    private double acotar(double valor, double minimo, double maximo) {
        return Math.max(minimo, Math.min(maximo, valor));
    }

    /** NUMERIC(4,1) en las tres columnas de temperatura y lluvia. */
    private BigDecimal unDecimal(double valor) {
        return BigDecimal.valueOf(valor).setScale(1, RoundingMode.HALF_UP);
    }
}
