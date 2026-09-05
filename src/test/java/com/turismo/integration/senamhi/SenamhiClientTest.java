package com.turismo.integration.senamhi;

import com.turismo.exception.FeedInvalidoException;
import com.turismo.integration.senamhi.dto.PrevisionClimaSenamhiDTO;
import com.turismo.model.Estacion;
import com.turismo.model.PrevisionClima;
import com.turismo.repository.EstacionRepository;
import com.turismo.service.ClimaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Caja Blanca: CB-03 (procesarFeedSenamhi - probabilidad de lluvia
 * valida, el registro se persiste) y CB-04 (procesarFeedSenamhi -
 * probabilidad fuera de rango -> excepcion, registro descartado).
 */
@ExtendWith(MockitoExtension.class)
class SenamhiClientTest {

    @Mock
    private RestClient senamhiRestClient;

    @Mock
    private ClimaService climaService;

    @Mock
    private EstacionRepository estacionRepository;

    @InjectMocks
    private SenamhiClient senamhiClient;

    private PrevisionClimaSenamhiDTO crearFeed(BigDecimal probabilidadLluvia) {
        PrevisionClimaSenamhiDTO feed = new PrevisionClimaSenamhiDTO();
        feed.setCodigoEstacion("CUS-OLL");
        feed.setFecha("2026-08-29");
        feed.setTemperaturaMinimaC(new BigDecimal("6.0"));
        feed.setTemperaturaMaximaC(new BigDecimal("14.5"));
        feed.setProbabilidadLluvia(probabilidadLluvia);
        feed.setEstadoClima("Parcialmente nublado");
        return feed;
    }

    private Estacion crearEstacion() {
        Estacion estacion = new Estacion();
        estacion.setId(3);
        estacion.setCodigo("CUS-OLL");
        estacion.setNombre("Estacion Ollantaytambo");
        return estacion;
    }

    /** CB-03: lluvia 45.0 dentro de 0-100 -> se guarda o actualiza PrevisionClima. */
    @Test
    void cb03_procesaElFeedCuandoLaProbabilidadDeLluviaEsValida() {
        Estacion estacion = crearEstacion();
        when(estacionRepository.findByCodigo("CUS-OLL")).thenReturn(Optional.of(estacion));

        senamhiClient.procesarFeedSenamhi(crearFeed(new BigDecimal("45.0")));

        ArgumentCaptor<PrevisionClima> captor = ArgumentCaptor.forClass(PrevisionClima.class);
        verify(climaService).guardarOActualizar(eq(estacion), eq(LocalDate.of(2026, 8, 29)), captor.capture());

        assertThat(captor.getValue().getProbabilidadLluvia()).isEqualByComparingTo("45.0");
        assertThat(captor.getValue().getTemperaturaMinC()).isEqualByComparingTo("6.0");
        assertThat(captor.getValue().getTemperaturaMaxC()).isEqualByComparingTo("14.5");
        assertThat(captor.getValue().getEstadoClima()).isEqualTo("Parcialmente nublado");
    }

    /** CB-04: lluvia 135.0 fuera de rango -> excepcion y nada se persiste. */
    @Test
    void cb04_descartaElRegistroCuandoLaProbabilidadDeLluviaEstaFueraDeRango() {
        PrevisionClimaSenamhiDTO feed = crearFeed(new BigDecimal("135.0"));

        assertThatThrownBy(() -> senamhiClient.procesarFeedSenamhi(feed))
                .isInstanceOf(FeedInvalidoException.class)
                .hasMessageContaining("fuera de rango");

        verify(climaService, never()).guardarOActualizar(any(), any(), any());
    }

    /** El rango es cerrado: 0 y 100 son valores validos del feed. */
    @Test
    void cb03_aceptaLosExtremosDelRangoDeLluvia() {
        when(estacionRepository.findByCodigo("CUS-OLL")).thenReturn(Optional.of(crearEstacion()));

        senamhiClient.procesarFeedSenamhi(crearFeed(BigDecimal.ZERO));
        senamhiClient.procesarFeedSenamhi(crearFeed(new BigDecimal("100")));

        verify(climaService, org.mockito.Mockito.times(2))
                .guardarOActualizar(any(), any(), any());
    }

    /** Un codigo de estacion desconocido se omite sin abortar el lote del dia. */
    @Test
    void omiteElRegistroCuandoLaEstacionNoEstaRegistrada() {
        when(estacionRepository.findByCodigo("CUS-OLL")).thenReturn(Optional.empty());

        senamhiClient.procesarFeedSenamhi(crearFeed(new BigDecimal("45.0")));

        verify(climaService, never()).guardarOActualizar(any(), any(), any());
    }
}
