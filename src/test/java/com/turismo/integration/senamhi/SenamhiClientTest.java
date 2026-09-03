package com.turismo.integration.senamhi;

import com.turismo.exception.FeedInvalidoException;
import com.turismo.integration.senamhi.dto.PrevisionClimaSenamhiDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Caja Blanca: CB-03 (procesarFeedSenamhi - probabilidad de lluvia
 * valida) y CB-04 (procesarFeedSenamhi - probabilidad fuera de rango
 * -> excepcion, registro descartado).
 */
@ExtendWith(MockitoExtension.class)
class SenamhiClientTest {

    @Mock
    private RestClient senamhiRestClient;

    @InjectMocks
    private SenamhiClient senamhiClient;

    private PrevisionClimaSenamhiDTO crearFeed(BigDecimal probabilidadLluvia) {
        PrevisionClimaSenamhiDTO feed = new PrevisionClimaSenamhiDTO();
        feed.setCodigoEstacion("OLL");
        feed.setFecha("2026-08-29");
        feed.setTemperaturaC(new BigDecimal("6.0"));
        feed.setProbabilidadLluvia(probabilidadLluvia);
        feed.setEstadoClima("Parcialmente nublado");
        return feed;
    }

    @Test
    void cb03_procesaElFeedCuandoLaProbabilidadDeLluviaEsValida() {
        PrevisionClimaSenamhiDTO feed = crearFeed(new BigDecimal("45.0"));

        assertThatCode(() -> senamhiClient.procesarFeedSenamhi(feed)).doesNotThrowAnyException();
    }

    @Test
    void cb04_descartaElRegistroCuandoLaProbabilidadDeLluviaEstaFueraDeRango() {
        PrevisionClimaSenamhiDTO feed = crearFeed(new BigDecimal("135.0"));

        assertThatThrownBy(() -> senamhiClient.procesarFeedSenamhi(feed))
                .isInstanceOf(FeedInvalidoException.class)
                .hasMessageContaining("fuera de rango");
    }
}
