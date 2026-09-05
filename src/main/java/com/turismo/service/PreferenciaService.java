package com.turismo.service;

import com.turismo.dto.PreferenciaDTO;
import com.turismo.dto.RutaCalculadaDTO;
import com.turismo.dto.ZonaResultadoDTO;
import com.turismo.exception.RutaInvalidaException;
import com.turismo.model.Estacion;
import com.turismo.model.ZonaTipoTurismo;
import com.turismo.model.ZonaTuristica;
import com.turismo.repository.ZonaTipoTurismoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RF-01/RF-03: procesa las preferencias del turista y filtra las zonas
 * turisticas cercanas a la estacion elegida segun los tipos de turismo
 * seleccionados (relacion N:M ZonaTipoTurismo), el tiempo disponible y la
 * dificultad aceptada.
 */
@Service
public class PreferenciaService {

    /** Orden de las tres dificultades del diccionario de datos (RutDificultad). */
    private static final Map<String, Integer> ORDEN_DIFICULTAD =
            Map.of("baja", 1, "media", 2, "alta", 3);

    private final ZonaTuristicaService zonaTuristicaService;
    private final ZonaTipoTurismoRepository zonaTipoTurismoRepository;
    private final RutaPeatonalService rutaPeatonalService;
    private final EstacionService estacionService;

    public PreferenciaService(ZonaTuristicaService zonaTuristicaService,
                               ZonaTipoTurismoRepository zonaTipoTurismoRepository,
                               RutaPeatonalService rutaPeatonalService,
                               EstacionService estacionService) {
        this.zonaTuristicaService = zonaTuristicaService;
        this.zonaTipoTurismoRepository = zonaTipoTurismoRepository;
        this.rutaPeatonalService = rutaPeatonalService;
        this.estacionService = estacionService;
    }

    /**
     * CU-02: devuelve las zonas alcanzables a pie desde la estacion de
     * partida que cumplen las tres preferencias del turista. Cada resultado
     * llega con su ruta de ida y vuelta ya calculada, de modo que la vista no
     * tenga que recalcularla zona por zona (RNF-01).
     */
    @Transactional(readOnly = true)
    public List<ZonaResultadoDTO> buscarZonasRecomendadas(PreferenciaDTO preferencia) {
        Estacion origen = estacionService.buscarActivaPorId(preferencia.getIdEstacionOrigen());
        List<ZonaTuristica> zonas = zonaTuristicaService.listarPorEstacion(origen.getId());

        List<ZonaResultadoDTO> resultados = new ArrayList<>();
        for (ZonaTuristica zona : zonas) {
            if (!coincideConPreferencias(zona, preferencia.getIdsTipoTurismo())) {
                continue;
            }
            RutaCalculadaDTO ruta;
            try {
                ruta = rutaPeatonalService.calcularRutaPeatonalIdaVuelta(origen, zona);
            } catch (RutaInvalidaException ex) {
                // La zona coincide con la estacion: no hay circuito caminable (CB-02).
                continue;
            }
            if (!cumpleTiempoDisponible(ruta, preferencia.getTiempoDisponibleMin())
                    || !cumpleDificultad(ruta, preferencia.getDificultad())) {
                continue;
            }
            resultados.add(mapearADto(zona, ruta));
        }
        resultados.sort((a, b) -> a.getRuta().getDistanciaKm().compareTo(b.getRuta().getDistanciaKm()));
        return resultados;
    }

    private boolean coincideConPreferencias(ZonaTuristica zona, List<Integer> idsTipoTurismo) {
        if (idsTipoTurismo == null || idsTipoTurismo.isEmpty()) {
            return true;
        }
        List<ZonaTipoTurismo> relaciones = zonaTipoTurismoRepository.findByZonaTuristica_Id(zona.getId());
        return relaciones.stream()
                .anyMatch(rel -> idsTipoTurismo.contains(rel.getTipoTurismo().getId()));
    }

    /** El circuito completo debe caber en el tiempo que declaro el turista. */
    private boolean cumpleTiempoDisponible(RutaCalculadaDTO ruta, Integer tiempoDisponibleMin) {
        return tiempoDisponibleMin == null || ruta.getTiempoEstimadoMin() <= tiempoDisponibleMin;
    }

    /** La dificultad elegida es un techo: quien acepta "Alta" tambien ve las mas suaves. */
    private boolean cumpleDificultad(RutaCalculadaDTO ruta, String dificultadMaxima) {
        if (dificultadMaxima == null || dificultadMaxima.isBlank()) {
            return true;
        }
        Integer techo = ORDEN_DIFICULTAD.get(dificultadMaxima.toLowerCase());
        Integer nivelRuta = ORDEN_DIFICULTAD.get(ruta.getDificultad().toLowerCase());
        return techo == null || nivelRuta == null || nivelRuta <= techo;
    }

    private ZonaResultadoDTO mapearADto(ZonaTuristica zona, RutaCalculadaDTO ruta) {
        ZonaResultadoDTO dto = new ZonaResultadoDTO();
        dto.setIdZona(zona.getId());
        dto.setNombre(zona.getNombre());
        dto.setDescripcion(zona.getDescripcion());
        dto.setCostoAproximado(zona.getCostoAprox());
        dto.setCupoMaximoDiario(zona.getCupoMaximoDiario());
        dto.setIdEstacionCercana(zona.getEstacionCercana().getId());
        dto.setLatitud(zona.getLatitud());
        dto.setLongitud(zona.getLongitud());
        dto.setRuta(ruta);
        dto.setTiposTurismo(zonaTipoTurismoRepository.findByZonaTuristica_Id(zona.getId())
                .stream()
                .map(rel -> rel.getTipoTurismo().getNombre())
                .collect(Collectors.toList()));
        return dto;
    }
}
