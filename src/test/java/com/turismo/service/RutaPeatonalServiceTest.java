package com.turismo.service;

import com.turismo.dto.RutaCalculadaDTO;
import com.turismo.exception.EstacionInactivaException;
import com.turismo.exception.RutaInvalidaException;
import com.turismo.model.Estacion;
import com.turismo.model.ZonaTuristica;
import com.turismo.repository.RutaPeatonalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Caja Blanca: CB-01 (calcularRutaPeatonalIdaVuelta - ruta valida) y
 * CB-02 (calcularRutaPeatonalIdaVuelta - distancia cero -> excepcion).
 *
 * La distancia se mide entre la estacion de origen y las coordenadas
 * propias de la zona turistica (seccion 5.1). Medirla contra la estacion
 * cercana de la zona daria cero siempre que el turista parta justamente de
 * esa estacion, que es el flujo normal del CU-02.
 */
@ExtendWith(MockitoExtension.class)
class RutaPeatonalServiceTest {

    @Mock
    private RutaPeatonalRepository rutaPeatonalRepository;

    @InjectMocks
    private RutaPeatonalService rutaPeatonalService;

    private Estacion crearEstacion(String lat, String lon) {
        Estacion estacion = new Estacion();
        estacion.setId(3);
        estacion.setNombre("Ollantaytambo");
        estacion.setLatitud(new BigDecimal(lat));
        estacion.setLongitud(new BigDecimal(lon));
        estacion.setEstado("Activa");
        return estacion;
    }

    private ZonaTuristica crearZona(String lat, String lon) {
        ZonaTuristica zona = new ZonaTuristica();
        zona.setId(1);
        zona.setNombre("Fortaleza de Ollantaytambo");
        zona.setLatitud(new BigDecimal(lat));
        zona.setLongitud(new BigDecimal(lon));
        return zona;
    }

    /** CB-01: coordenadas validas y distancia de ida > 0 -> circuito duplicado. */
    @Test
    void cb01_calculaCircuitoIdaVueltaCuandoLaDistanciaEsMayorACero() {
        // Estación Ollantaytambo y la fortaleza, separadas por 0.60 km de ida.
        Estacion origen = crearEstacion("-13.258600", "-72.265000");
        ZonaTuristica destino = crearZona("-13.254466", "-72.268563");

        RutaCalculadaDTO ruta = rutaPeatonalService.calcularRutaPeatonalIdaVuelta(origen, destino);

        assertThat(ruta.getEsIdaVuelta()).isTrue();
        assertThat(ruta.getDistanciaKm()).isEqualByComparingTo(new BigDecimal("1.20"));
        assertThat(ruta.getTiempoEstimadoMin()).isEqualTo(14);
        assertThat(ruta.getDificultad()).isEqualTo("Baja");
    }

    /** CB-02: la zona coincide con la estacion -> distancia de ida cero. */
    @Test
    void cb02_lanzaExcepcionCuandoLaDistanciaDeIdaEsCero() {
        Estacion origen = crearEstacion("-13.154700", "-72.525000");
        ZonaTuristica destino = crearZona("-13.154700", "-72.525000");

        assertThatThrownBy(() -> rutaPeatonalService.calcularRutaPeatonalIdaVuelta(origen, destino))
                .isInstanceOf(RutaInvalidaException.class)
                .hasMessageContaining("distancia caminable mayor a cero");
    }

    /** La dificultad sube con la distancia del circuito completo (RF-05). */
    @Test
    void clasificaLaDificultadSegunLaDistanciaDelCircuito() {
        Estacion origen = crearEstacion("-13.154700", "-72.525000");

        // 5.00 km de ida -> 10.00 km de circuito -> por encima del umbral alto.
        RutaCalculadaDTO larga = rutaPeatonalService.calcularRutaPeatonalIdaVuelta(
                origen, crearZona("-13.193641", "-72.548093"));
        assertThat(larga.getDificultad()).isEqualTo("Alta");

        // 1.90 km de ida -> 3.80 km de circuito -> franja media.
        RutaCalculadaDTO media = rutaPeatonalService.calcularRutaPeatonalIdaVuelta(
                origen, crearZona("-13.156189", "-72.542481"));
        assertThat(media.getDificultad()).isEqualTo("Media");
    }

    /** RF-02 (CN-02): una estacion inactiva no puede ser punto de partida. */
    @Test
    void rechazaLaEstacionDeOrigenInactiva() {
        Estacion origen = crearEstacion("-13.474400", "-72.042800");
        origen.setNombre("Estacion Poroy");
        origen.setEstado("Inactiva");

        assertThatThrownBy(() -> rutaPeatonalService.calcularRutaPeatonalIdaVuelta(
                origen, crearZona("-13.254466", "-72.268563")))
                .isInstanceOf(EstacionInactivaException.class)
                .hasMessageContaining("inactiva");
    }
}
