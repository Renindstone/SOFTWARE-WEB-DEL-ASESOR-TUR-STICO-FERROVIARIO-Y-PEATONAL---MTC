package com.turismo.integration.perurail;

import com.turismo.exception.TarifaInvalidaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Caja Blanca: CB-05 (validarTarifaPeruRail - tarifa valida) y
 * CB-06 (validarTarifaPeruRail - tarifa negativa/cero -> excepcion).
 */
@ExtendWith(MockitoExtension.class)
class PeruRailClientTest {

    @Mock
    private RestClient peruRailRestClient;

    @InjectMocks
    private PeruRailClient peruRailClient;

    @Test
    void cb05_habilitaLaPersistenciaCuandoLaTarifaEsPositiva() {
        boolean resultado = peruRailClient.validarTarifaPeruRail(new BigDecimal("65.00"));

        assertThat(resultado).isTrue();
    }

    @Test
    void cb06_rechazaLaTarifaCuandoEsNegativaOCero() {
        assertThatThrownBy(() -> peruRailClient.validarTarifaPeruRail(new BigDecimal("-10.00")))
                .isInstanceOf(TarifaInvalidaException.class)
                .hasMessageContaining("mayor a cero");
    }
}
