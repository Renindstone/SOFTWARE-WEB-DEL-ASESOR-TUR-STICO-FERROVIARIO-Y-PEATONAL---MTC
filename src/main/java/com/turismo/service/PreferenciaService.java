package com.turismo.service;

import com.turismo.dto.PreferenciaDTO;
import com.turismo.dto.ZonaResultadoDTO;
import com.turismo.model.ZonaTipoTurismo;
import com.turismo.model.ZonaTuristica;
import com.turismo.repository.ZonaTipoTurismoRepository;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired 
    private ZonaTuristicaService zonaTuristicaService;
    @Autowired
    private ZonaTipoTurismoRepository zonaTipoTurismoRepository;

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
        List<ZonaTipoTurismo> relaciones = zonaTipoTurismoRepository.findByZonaTuristica_Id(zona.getId());
        return relaciones.stream()
                .anyMatch(rel -> idsTipoTurismo.contains(rel.getTipoTurismo().getId()));
    }

    private ZonaResultadoDTO mapearADto(ZonaTuristica zona) {     
        ZonaResultadoDTO dto = new ZonaResultadoDTO();
        dto.setIdZona(zona.getId());
        dto.setNombre(zona.getNombre());
        dto.setDescripcion(zona.getDescripcion());
        dto.setCostoAproximado(zona.getCostoAprox());
        dto.setCupoMaximoDiario(zona.getCupoMaximoDiario());
        dto.setIdEstacionCercana(zona.getEstacionCercana().getId());
        dto.setTiposTurismo(zonaTipoTurismoRepository.findByZonaTuristica_Id(zona.getId())
                .stream()
                .map(rel -> rel.getTipoTurismo().getNombre()) 
                .collect(Collectors.toList()));
        return dto;
    }
}
