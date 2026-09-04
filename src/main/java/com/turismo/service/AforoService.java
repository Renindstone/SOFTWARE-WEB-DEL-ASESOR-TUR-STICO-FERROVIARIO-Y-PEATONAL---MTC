package com.turismo.service;

import com.turismo.exception.AforoCompletoException;
import com.turismo.model.ControlAforo;
import com.turismo.model.ZonaTuristica;
import com.turismo.repository.ControlAforoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * RF-16/RF-17/RNF-08: valida y actualiza de forma atomica el cupo maximo
 * diario de una zona turistica antes de confirmar el informe de visita.
 * Caja Blanca: CB-08 (cupo disponible), CB-09 (cupo agotado -> excepcion).
 */
@Service
public class AforoService {

    private final ControlAforoRepository controlAforoRepository;

    public AforoService(ControlAforoRepository controlAforoRepository) {
        this.controlAforoRepository = controlAforoRepository;
    }

    /**
     * CB-08/CB-09: si la zona no tiene ZonCupoMaximoDiario configurado, no
     * aplica la validacion (RF-16). Si lo tiene, incrementa el contador de
     * forma atomica cuando hay cupo disponible; si ya se alcanzo el
     * maximo, rechaza la operacion con AforoCompletoException.
     */
    @Transactional
    public boolean validarAforoDisponible(ZonaTuristica zona, LocalDate fecha) {
        if (zona.getCupoMaximoDiario() == null) {
            return true;
        }

        ControlAforo control = controlAforoRepository
                .findByZona_IdAndFecha(zona.getId(), fecha)
                .orElseGet(() -> {
                    ControlAforo nuevo = new ControlAforo();
                    nuevo.setZona(zona);
                    nuevo.setFecha(fecha);
                    nuevo.setCupoUtilizado(0);
                    return nuevo;
                });

        if (control.getCupoUtilizado() >= zona.getCupoMaximoDiario()) {
            throw new AforoCompletoException(
                    "El aforo de la zona para la fecha seleccionada ya fue alcanzado");
        }

        control.setCupoUtilizado(control.getCupoUtilizado() + 1);
        controlAforoRepository.save(control);
        return true;
    }
}
