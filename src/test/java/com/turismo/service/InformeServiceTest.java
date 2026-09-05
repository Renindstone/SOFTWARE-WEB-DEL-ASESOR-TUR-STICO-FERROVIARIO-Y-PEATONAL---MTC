package com.turismo.service;

import com.turismo.dto.InformeConsolidadoDTO;
import com.turismo.exception.AforoCompletoException;
import com.turismo.model.Estacion;
import com.turismo.model.InformePlanificacion;
import com.turismo.model.PrevisionClima;
import com.turismo.model.RutaPeatonal;
import com.turismo.model.ServicioTren;
import com.turismo.model.ZonaTuristica;
import com.turismo.repository.InformePlanificacionRepository;
import com.turismo.util.GeneradorPdf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * RF-08 (CN-07/CN-09): generacion del informe consolidado. Cubre que el
 * informe reune ruta, clima y tarifa, que se persiste con codigo
 * correlativo, y que el aforo agotado corta la emision antes de guardar.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InformeServiceTest {

    private static final LocalDate FECHA_VISITA = LocalDate.of(2026, 9, 2);

    @Mock
    private InformePlanificacionRepository informePlanificacionRepository;
    @Mock
    private RutaPeatonalService rutaPeatonalService;
    @Mock
    private ClimaService climaService;
    @Mock
    private AforoService aforoService;
    @Mock
    private AuditoriaService auditoriaService;
    @Mock
    private GeneradorPdf generadorPdf;

    @InjectMocks
    private InformeService informeService;

    private Estacion origen;
    private ZonaTuristica destino;
    private ServicioTren servicio;

    @BeforeEach
    void prepararEscenario() {
        origen = new Estacion();
        origen.setId(4);
        origen.setNombre("Estacion Ollantaytambo");
        origen.setLatitud(new BigDecimal("-13.258600"));
        origen.setLongitud(new BigDecimal("-72.265000"));
        origen.setEstado("Activa");

        destino = new ZonaTuristica();
        destino.setId(6);
        destino.setNombre("Conjunto Arqueologico de Ollantaytambo");
        destino.setLatitud(new BigDecimal("-13.254466"));
        destino.setLongitud(new BigDecimal("-72.268563"));
        destino.setCostoAprox(new BigDecimal("70.00"));

        servicio = new ServicioTren();
        servicio.setId(3);
        servicio.setTarifa(new BigDecimal("145.00"));

        // El calculo real de la ruta vive en RutaPeatonalServiceTest (CB-01/CB-02).
        when(rutaPeatonalService.calcularRutaPeatonalIdaVuelta(any(), any()))
                .thenAnswer(invocacion -> {
                    var dto = new com.turismo.dto.RutaCalculadaDTO();
                    dto.setNombre("Circuito Ollantaytambo - Fortaleza");
                    dto.setDistanciaKm(new BigDecimal("1.20"));
                    dto.setTiempoEstimadoMin(14);
                    dto.setDificultad("Baja");
                    return dto;
                });
        when(rutaPeatonalService.obtenerOCrearRuta(any(), any(), any())).thenReturn(new RutaPeatonal());
        when(informePlanificacionRepository.save(any(InformePlanificacion.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));
        when(informePlanificacionRepository.findTopByOrderByIdDesc()).thenReturn(Optional.empty());
        when(aforoService.consultarCupoDisponible(any(), any())).thenReturn(Optional.empty());
    }

    /** CN-07: el informe reune ruta, clima, tarifa y total estimado. */
    @Test
    void generaElInformeConRutaClimaYTarifa() {
        PrevisionClima clima = new PrevisionClima();
        clima.setTemperaturaMinC(new BigDecimal("5.0"));
        clima.setTemperaturaMaxC(new BigDecimal("19.4"));
        clima.setProbabilidadLluvia(new BigDecimal("30.0"));
        clima.setEstadoClima("Parcialmente nublado");
        when(climaService.buscarPorEstacionYFecha(4, FECHA_VISITA)).thenReturn(Optional.of(clima));

        InformeConsolidadoDTO informe = informeService.generarInformeConsolidado(
                origen, destino, servicio, FECHA_VISITA, null);

        assertThat(informe.getEstacionOrigen()).isEqualTo("Estacion Ollantaytambo");
        assertThat(informe.getZonaDestino()).isEqualTo("Conjunto Arqueologico de Ollantaytambo");
        assertThat(informe.getRuta().getDistanciaKm()).isEqualByComparingTo("1.20");
        assertThat(informe.getRuta().getDificultad()).isEqualTo("Baja");
        // Las temperaturas del SENAMHI deben llegar al informe, no solo la lluvia.
        assertThat(informe.getTemperaturaMinimaC()).isEqualByComparingTo("5.0");
        assertThat(informe.getTemperaturaMaximaC()).isEqualByComparingTo("19.4");
        assertThat(informe.getEstadoClima()).isEqualTo("Parcialmente nublado");
        assertThat(informe.getTarifaTren()).isEqualByComparingTo("145.00");
        // InfTotalEstimado = tarifa del tren + costo aproximado de la zona.
        assertThat(informe.getTotalEstimado()).isEqualByComparingTo("215.00");
    }

    /** El codigo sigue el formato INF-0001 del diccionario de datos (6.4). */
    @Test
    void persisteElInformeConCodigoCorrelativo() {
        InformePlanificacion ultimo = new InformePlanificacion();
        ultimo.setCodigo("INF-0007");
        when(informePlanificacionRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(ultimo));
        when(climaService.buscarPorEstacionYFecha(4, FECHA_VISITA)).thenReturn(Optional.empty());

        InformeConsolidadoDTO informe = informeService.generarInformeConsolidado(
                origen, destino, servicio, FECHA_VISITA, null);

        ArgumentCaptor<InformePlanificacion> captor = ArgumentCaptor.forClass(InformePlanificacion.class);
        verify(informePlanificacionRepository).save(captor.capture());

        assertThat(captor.getValue().getCodigo()).isEqualTo("INF-0008");
        assertThat(informe.getCodigo()).isEqualTo("INF-0008");
        // Consulta anonima: InfIdUsuario queda NULL (diccionario 6.4).
        assertThat(captor.getValue().getUsuario()).isNull();
        assertThat(captor.getValue().getFechaVisita()).isEqualTo(FECHA_VISITA);
    }

    /** CN-09: con el aforo agotado no se emite ni se persiste informe alguno. */
    @Test
    void noPersisteElInformeCuandoElAforoEstaCompleto() {
        doThrow(new AforoCompletoException("El aforo de la zona para la fecha seleccionada ya fue alcanzado"))
                .when(aforoService).validarAforoDisponible(destino, FECHA_VISITA);

        assertThatThrownBy(() -> informeService.generarInformeConsolidado(
                origen, destino, servicio, FECHA_VISITA, null))
                .isInstanceOf(AforoCompletoException.class);

        verify(informePlanificacionRepository, never()).save(any());
    }

    /**
     * La descarga en PDF de un informe ya emitido no vuelve a descontar el
     * aforo: ver el HTML y bajar el PDF consumirian dos cupos.
     */
    @Test
    void laPrevisualizacionNoConsumeAforoNiPersisteInforme() {
        when(climaService.buscarPorEstacionYFecha(4, FECHA_VISITA)).thenReturn(Optional.empty());

        InformeConsolidadoDTO informe = informeService.previsualizarInforme(
                origen, destino, servicio, FECHA_VISITA, "INF-0008");

        assertThat(informe.getCodigo()).isEqualTo("INF-0008");
        verify(aforoService, never()).validarAforoDisponible(any(), any());
        verify(informePlanificacionRepository, never()).save(any());
    }
}
