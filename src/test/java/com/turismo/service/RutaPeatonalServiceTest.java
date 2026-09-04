package com.turismo.service;

import com.turismo.dto.RutaCalculadaDTO;
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
 */
@ExtendWith(MockitoExtension.class)
class RutaPeatonalServiceTest {

    @Mock
    private RutaPeatonalRepository rutaPeatonalRepository;

    @InjectMocks
    private RutaPeatonalService rutaPeatonalService;

    private Estacion crearEstacion(BigDecimal lat, BigDecimal lon) {
        Estacion estacion = new Estacion();
        estacion.setId(3);
        estacion.setNombre("Ollantaytambo");
        estacion.setLatitud(lat);
        estacion.setLongitud(lon);
        return estacion;
    }

    private ZonaTuristica crearZona(Estacion estacionCercana) {
        ZonaTuristica zona = new ZonaTuristica();
        zona.setId(1);
        zona.setNombre("Fortaleza de Ollantaytambo");
        zona.setEstacionCercana(estacionCercana);
        return zona;
    }

    @Test
    void cb01_calculaCircuitoIdaVueltaCuandoLaDistanciaEsMayorACero() {
        Estacion origen = crearEstacion(new BigDecimal("-13.259300"), new BigDecimal("-72.263100"));
        Estacion cercanaZona = crearEstacion(new BigDecimal("-13.267500"), new BigDecimal("-72.267400"));
        ZonaTuristica destino = crearZona(cercanaZona);

        RutaCalculadaDTO ruta = rutaPeatonalService.calcularRutaPeatonalIdaVuelta(origen, destino);

        assertThat(ruta.getEsIdaVuelta()).isTrue();
        assertThat(ruta.getDistanciaKm()).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void cb02_lanzaExcepcionCuandoLaDistanciaDeIdaEsCero() {
        Estacion origen = crearEstacion(new BigDecimal("-13.152500"), new BigDecimal("-72.525000"));
        ZonaTuristica destino = crearZona(origen);

        assertThatThrownBy(() -> rutaPeatonalService.calcularRutaPeatonalIdaVuelta(origen, destino))
                .isInstanceOf(RutaInvalidaException.class)
                .hasMessageContaining("distancia caminable mayor a cero");
    }
}
