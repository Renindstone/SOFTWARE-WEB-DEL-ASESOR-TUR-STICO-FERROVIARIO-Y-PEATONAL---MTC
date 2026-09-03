package com.turismo.service;

import com.turismo.dto.InformeConsolidadoDTO;
import com.turismo.dto.RutaCalculadaDTO;
import com.turismo.model.*;
import com.turismo.repository.InformePlanificacionRepository;
import com.turismo.util.GeneradorPdf;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RF-08: genera el informe consolidado (ruta, clima, tiempo estimado,
 * dificultad y tarifa del tren) en formato web/PDF descargable.
 * Orquesta RutaPeatonalService, ClimaService, AforoService (RF-16/RF-17)
 * y AuditoriaService antes de persistir InformePlanificacion.
 */
@Service
public class InformeService {

    private static final AtomicInteger CONTADOR = new AtomicInteger(1);
    private static final DateTimeFormatter FORMATO_CODIGO = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final InformePlanificacionRepository informePlanificacionRepository;
    private final RutaPeatonalService rutaPeatonalService;
    private final ClimaService climaService;
    private final AforoService aforoService;
    private final AuditoriaService auditoriaService;
    private final GeneradorPdf generadorPdf;

    public InformeService(InformePlanificacionRepository informePlanificacionRepository,
                           RutaPeatonalService rutaPeatonalService,
                           ClimaService climaService,
                           AforoService aforoService,
                           AuditoriaService auditoriaService,
                           GeneradorPdf generadorPdf) {
        this.informePlanificacionRepository = informePlanificacionRepository;
        this.rutaPeatonalService = rutaPeatonalService;
        this.climaService = climaService;
        this.aforoService = aforoService;
        this.auditoriaService = auditoriaService;
        this.generadorPdf = generadorPdf;
    }

    /**
     * RF-08/RF-16: valida el aforo de la zona (AforoService), calcula la
     * ruta y arma el InformeConsolidadoDTO con clima y tarifa. Si el aforo
     * esta completo, AforoCompletoException interrumpe la generacion.
     */
    @Transactional
    public InformeConsolidadoDTO generarInformeConsolidado(Estacion origen, ZonaTuristica destino,
                                                             ServicioTren servicioTren, LocalDate fechaVisita) {
        aforoService.validarAforoDisponible(destino, fechaVisita);

        RutaCalculadaDTO ruta = rutaPeatonalService.calcularRutaPeatonalIdaVuelta(origen, destino);

        Optional<PrevisionClima> clima = climaService.buscarPorEstacionYFecha(origen.getEstIdEstacion(), fechaVisita);

        InformeConsolidadoDTO informe = new InformeConsolidadoDTO();
        informe.setCodigo(generarCodigo());
        informe.setFechaVisita(fechaVisita);
        informe.setEstacionOrigen(origen.getEstNombre());
        informe.setZonaDestino(destino.getZonNombre());
        informe.setRuta(ruta);
        clima.ifPresent(c -> {
            informe.setTemperaturaMinimaC(c.getCliTemperaturaMinimaC());
            informe.setTemperaturaMaximaC(c.getCliTemperaturaMaximaC());
            informe.setProbabilidadLluvia(c.getCliProbabilidadLluvia());
            informe.setEstadoClima(c.getCliEstadoClima());
        });
        if (servicioTren != null) {
            informe.setTarifaTren(servicioTren.getSerTarifa());
        }
        informe.setTotalEstimado(calcularTotalEstimado(destino, servicioTren));

        return informe;
    }

    private BigDecimal calcularTotalEstimado(ZonaTuristica destino, ServicioTren servicioTren) {
        BigDecimal total = BigDecimal.ZERO;
        if (destino.getZonCostoAprox() != null) {
            total = total.add(destino.getZonCostoAprox());
        }
        if (servicioTren != null && servicioTren.getSerTarifa() != null) {
            total = total.add(servicioTren.getSerTarifa());
        }
        return total;
    }

    private String generarCodigo() {
        return "INF-" + LocalDate.now().format(FORMATO_CODIGO) + "-" + CONTADOR.getAndIncrement();
    }

    public byte[] exportarPdf(InformeConsolidadoDTO informe) {
        return generadorPdf.generar(informe);
    }
}
