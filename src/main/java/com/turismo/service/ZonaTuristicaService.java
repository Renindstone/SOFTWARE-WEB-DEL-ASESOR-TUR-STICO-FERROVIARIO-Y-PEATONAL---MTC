package com.turismo.service;

import com.turismo.model.TipoTurismo;
import com.turismo.model.ZonaTipoTurismo;
import com.turismo.model.ZonaTuristica;
import com.turismo.repository.TipoTurismoRepository;
import com.turismo.repository.ZonaTipoTurismoRepository;
import com.turismo.repository.ZonaTuristicaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * RF-10/RF-17: CRUD de zonas turisticas para Travel Group Peru, incluyendo
 * la asignacion N:M con TipoTurismo (tabla ZonaTipoTurismo) y el campo
 * ZonCupoMaximoDiario usado por AforoService.
 */
@Service
public class ZonaTuristicaService {

    @Autowired 
    private ZonaTuristicaRepository zonaTuristicaRepository;
    @Autowired
    private ZonaTipoTurismoRepository zonaTipoTurismoRepository;
    @Autowired
    private TipoTurismoRepository tipoTurismoRepository;

    public List<ZonaTuristica> listarActivas() {
        return zonaTuristicaRepository.findByEstado("Activa");
    }

    public List<ZonaTuristica> listarPorEstacion(Integer idEstacionCercana) {
        return zonaTuristicaRepository.findByEstacionCercana_Id(idEstacionCercana);
    }

    public Optional<ZonaTuristica> buscarPorId(Integer id) {
        return zonaTuristicaRepository.findById(id);
    }

    @Transactional
    public ZonaTuristica registrarOActualizar(ZonaTuristica zona, List<Integer> idsTipoTurismo) {
        ZonaTuristica guardada = zonaTuristicaRepository.save(zona);
        asignarTiposTurismo(guardada, idsTipoTurismo);
        return guardada;
    }

    @Transactional
    public void asignarTiposTurismo(ZonaTuristica zona, List<Integer> idsTipoTurismo) {
        zonaTipoTurismoRepository.deleteAll(
                zonaTipoTurismoRepository.findByZonaTuristica_Id(zona.getId()));

        for (Integer idTipo : idsTipoTurismo) {
            TipoTurismo tipo = tipoTurismoRepository.findById(idTipo)
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de turismo no encontrado: " + idTipo));
            ZonaTipoTurismo relacion = new ZonaTipoTurismo();
            relacion.setZonaTuristica(zona);
            relacion.setTipoTurismo(tipo);
            zonaTipoTurismoRepository.save(relacion);
        }
    }
}
