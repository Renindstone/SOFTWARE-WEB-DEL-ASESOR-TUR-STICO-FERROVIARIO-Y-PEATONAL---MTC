package com.turismo.service;

import com.turismo.exception.AforoCompletoException;
import com.turismo.model.ControlAforo;
import com.turismo.model.ZonaTuristica;
import com.turismo.repository.ControlAforoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Caja Blanca: CB-08 (validarAforoDisponible - cupo disponible, se
 * incrementa el contador) y CB-09 (validarAforoDisponible - cupo
 * agotado -> excepcion, sin incrementar el contador).
 */
@ExtendWith(MockitoExtension.class)
class AforoServiceTest {

    @Mock
    private ControlAforoRepository controlAforoRepository;

    @InjectMocks
    private AforoService aforoService;

    private ZonaTuristica crearZona(int cupoMaximo) {
        ZonaTuristica zona = new ZonaTuristica();
        zona.setId(1);
        zona.setNombre("Fortaleza de Ollantaytambo");
        zona.setCupoMaximoDiario(cupoMaximo);
        return zona;
    }

    @Test
    void cb08_incrementaElContadorCuandoElCupoUtilizadoEsMenorAlMaximo() {
        ZonaTuristica zona = crearZona(500);
        LocalDate fecha = LocalDate.of(2026, 8, 30);
        ControlAforo control = new ControlAforo();
        control.setZona(zona);
        control.setFecha(fecha);
        control.setCupoUtilizado(320);

        when(controlAforoRepository.findByZona_IdAndFecha(1, fecha))
                .thenReturn(Optional.of(control));
        when(controlAforoRepository.save(any(ControlAforo.class))).thenAnswer(inv -> inv.getArgument(0));

        boolean resultado = aforoService.validarAforoDisponible(zona, fecha);

        assertThat(resultado).isTrue();
        assertThat(control.getCupoUtilizado()).isEqualTo(321);
    }

    @Test
    void cb09_rechazaLaOperacionCuandoElCupoYaFueAlcanzado() {
        ZonaTuristica zona = crearZona(500);
        LocalDate fecha = LocalDate.of(2026, 8, 30);
        ControlAforo control = new ControlAforo();
        control.setZona(zona);
        control.setFecha(fecha);
        control.setCupoUtilizado(500);

        when(controlAforoRepository.findByZona_IdAndFecha(1, fecha))
                .thenReturn(Optional.of(control));

        assertThatThrownBy(() -> aforoService.validarAforoDisponible(zona, fecha))
                .isInstanceOf(AforoCompletoException.class)
                .hasMessageContaining("aforo");
        assertThat(control.getCupoUtilizado()).isEqualTo(500);
    }
}
