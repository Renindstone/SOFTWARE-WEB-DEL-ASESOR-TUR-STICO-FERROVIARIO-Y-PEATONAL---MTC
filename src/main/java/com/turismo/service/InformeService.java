package com.turismo.service;

import com.turismo.dto.InformeConsolidadoDTO;
import com.turismo.dto.RutaCalculadaDTO;
import com.turismo.model.Estacion;
import com.turismo.model.InformePlanificacion;
import com.turismo.model.PrevisionClima;
import com.turismo.model.RutaPeatonal;
import com.turismo.model.ServicioTren;
import com.turismo.model.Usuario;
import com.turismo.model.ZonaTuristica;
import com.turismo.repository.InformePlanificacionRepository;
import com.turismo.util.GeneradorPdf;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * RF-08: genera el informe consolidado (ruta, clima, tiempo estimado,
 * dificultad y tarifa del tren) en formato web/PDF descargable.
 * Orquesta RutaPeatonalService, ClimaService, AforoService (RF-16/RF-17)
 * y AuditoriaService antes de persistir InformePlanificacion.
 */
@Service
public class InformeService {

    private static final String PREFIJO_CODIGO = "INF-";
    private static final String TABLA_AUDITADA = "informe_planificacion";

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
     * RF-08/RF-16 (CU-08): valida el aforo de la zona (AforoService), calcula
     * y persiste la ruta, arma el InformeConsolidadoDTO con clima y tarifa y
     * guarda el InformePlanificacion. Si el aforo esta completo,
     * AforoCompletoException interrumpe la generacion antes de consumir cupo.
     */
    @Transactional
    public InformeConsolidadoDTO generarInformeConsolidado(Estacion origen, ZonaTuristica destino,
                                                            ServicioTren servicioTren, LocalDate fechaVisita,
                                                            Usuario usuario) {
        aforoService.validarAforoDisponible(destino, fechaVisita);

        InformeConsolidadoDTO informe = prepararInforme(origen, destino, servicioTren, fechaVisita);
        RutaPeatonal ruta = rutaPeatonalService.obtenerOCrearRuta(origen, destino, informe.getRuta());

        InformePlanificacion registro = new InformePlanificacion();
        registro.setCodigo(generarCodigo());
        registro.setFechaEmision(LocalDateTime.now());
        registro.setFechaVisita(fechaVisita);
        registro.setUsuario(usuario);
        registro.setRuta(ruta);
        registro.setTotalEstimado(informe.getTotalEstimado());
        informePlanificacionRepository.save(registro);

        informe.setCodigo(registro.getCodigo());

        auditoriaService.registrarAuditoria(
                usuario == null ? "ANONIMO" : usuario.getNombreUsuario(),
                "INSERT", TABLA_AUDITADA, null,
                "InfCodigo=" + registro.getCodigo()
                        + "; ZonNombre=" + destino.getNombre()
                        + "; InfFechaVisita=" + fechaVisita);

        return informe;
    }

    /**
     * Vista previa del informe: mismos datos, pero sin consumir cupo de aforo
     * ni persistir el InformePlanificacion. La usa la exportacion a PDF de un
     * informe ya emitido, para que ver el HTML y descargar el PDF no descuente
     * el aforo dos veces.
     */
    @Transactional(readOnly = true)
    public InformeConsolidadoDTO previsualizarInforme(Estacion origen, ZonaTuristica destino,
                                                       ServicioTren servicioTren, LocalDate fechaVisita,
                                                       String codigoExistente) {
        InformeConsolidadoDTO informe = prepararInforme(origen, destino, servicioTren, fechaVisita);
        if (codigoExistente != null && !codigoExistente.isBlank()) {
            informe.setCodigo(codigoExistente);
        }
        return informe;
    }

    private InformeConsolidadoDTO prepararInforme(Estacion origen, ZonaTuristica destino,
                                                   ServicioTren servicioTren, LocalDate fechaVisita) {
        RutaCalculadaDTO ruta = rutaPeatonalService.calcularRutaPeatonalIdaVuelta(origen, destino);
        Optional<PrevisionClima> clima = climaService.buscarPorEstacionYFecha(origen.getId(), fechaVisita);

        InformeConsolidadoDTO informe = new InformeConsolidadoDTO();
        informe.setFechaVisita(fechaVisita);
        informe.setEstacionOrigen(origen.getNombre());
        informe.setZonaDestino(destino.getNombre());
        informe.setRuta(ruta);
        clima.ifPresent(c -> {
            informe.setTemperaturaMinimaC(c.getTemperaturaMinC());
            informe.setTemperaturaMaximaC(c.getTemperaturaMaxC());
            informe.setProbabilidadLluvia(c.getProbabilidadLluvia());
            informe.setEstadoClima(c.getEstadoClima());
        });
        if (servicioTren != null) {
            informe.setTarifaTren(servicioTren.getTarifa());
        }
        informe.setCostoZona(destino.getCostoAprox());
        informe.setTotalEstimado(calcularTotalEstimado(destino, servicioTren));
        aforoService.consultarCupoDisponible(destino, fechaVisita)
                .ifPresent(informe::setCupoDisponible);
        return informe;
    }

    private BigDecimal calcularTotalEstimado(ZonaTuristica destino, ServicioTren servicioTren) {
        BigDecimal total = BigDecimal.ZERO;
        if (destino.getCostoAprox() != null) {
            total = total.add(destino.getCostoAprox());
        }
        if (servicioTren != null && servicioTren.getTarifa() != null) {
            total = total.add(servicioTren.getTarifa());
        }
        return total;
    }

    /**
     * Codigo correlativo con el formato INF-0001 del diccionario de datos
     * (InfCodigo, VARCHAR(15)). Se deriva del ultimo codigo persistido, de
     * modo que sobreviva a los reinicios de la aplicacion.
     */
    private String generarCodigo() {
        int siguiente = informePlanificacionRepository.findTopByOrderByIdDesc()
                .map(InformePlanificacion::getCodigo)
                .map(this::extraerCorrelativo)
                .orElse(0) + 1;
        return String.format("%s%04d", PREFIJO_CODIGO, siguiente);
    }

    private int extraerCorrelativo(String codigo) {
        String digitos = codigo.replaceAll("\\D", "");
        return digitos.isEmpty() ? 0 : Integer.parseInt(digitos);
    }

    public byte[] exportarPdf(InformeConsolidadoDTO informe) {
        return generadorPdf.generar(informe);
    }
}
