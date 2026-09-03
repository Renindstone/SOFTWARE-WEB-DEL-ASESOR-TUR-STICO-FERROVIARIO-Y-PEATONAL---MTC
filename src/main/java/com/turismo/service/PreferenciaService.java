package com.turismo.service;

import com.turismo.dto.PreferenciaDTO;
import com.turismo.dto.ZonaResultadoDTO;
import com.turismo.model.ZonaTipoTurismo;
import com.turismo.model.ZonaTuristica;
import com.turismo.repository.ZonaTipoTurismoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * RF-01/RF-03: procesa las preferencias del turista y filtra las zonas
 * turisticas cercanas a la estacion elegida segun los tipos de turismo
 * seleccionados (relacion N:M ZonaTipoTurismo).
 */
@Service
public class PreferenciaService {

    private final ZonaTuristicaService zonaTuristicaService;
    private final ZonaTipoTurismoRepository zonaTipoTurismoRepository;

    public PreferenciaService(ZonaTuristicaService zonaTuristicaService,
                               ZonaTipoTurismoRepository zonaTipoTurismoRepository) {
        this.zonaTuristicaService = zonaTuristicaService;
        this.zonaTipoTurismoRepository = zonaTipoTurismoRepository;
    }

    public List<ZonaResultadoDTO> buscarZonasRecomendadas(PreferenciaDTO preferencia) {
        List<ZonaTuristica> zonas = zonaTuristicaService.listarPorEstacion(preferencia.getIdEstacionOrigen());

        return zonas.stream()
                .filter(zona -> coincideConPreferencias(zona, preferencia.getIdsTipoTurismo()))
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    private boolean coincideConPreferencias(ZonaTuristica zona, List<Integer> idsTipoTurismo) {
        if (idsTipoTurismo == null || idsTipoTurismo.isEmpty()) {
            return true;
        }
        List<ZonaTipoTurismo> relaciones = zonaTipoTurismoRepository.findByZonaTuristica_ZonIdZona(zona.getZonIdZona());
        return relaciones.stream()
                .anyMatch(rel -> idsTipoTurismo.contains(rel.getTipoTurismo().getTipIdTipoTurismo()));
    }

    private ZonaResultadoDTO mapearADto(ZonaTuristica zona) {
        ZonaResultadoDTO dto = new ZonaResultadoDTO();
        dto.setIdZona(zona.getZonIdZona());
        dto.setNombre(zona.getZonNombre());
        dto.setDescripcion(zona.getZonDescripcion());
        dto.setCostoAproximado(zona.getZonCostoAprox());
        dto.setCupoMaximoDiario(zona.getZonCupoMaximoDiario());
        dto.setIdEstacionCercana(zona.getEstacionCercana().getEstIdEstacion());
        dto.setTiposTurismo(zonaTipoTurismoRepository.findByZonaTuristica_ZonIdZona(zona.getZonIdZona())
                .stream()
                .map(rel -> rel.getTipoTurismo().getTipNombre())
                .collect(Collectors.toList()));
        return dto;
    }
}
