package com.turismo.service;

import com.turismo.exception.AforoCompletoException;
import com.turismo.model.ControlAforo;
import com.turismo.model.ZonaTuristica;
import com.turismo.repository.ControlAforoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

/**
 * RF-16/RF-17/RNF-08: valida y actualiza de forma atomica el cupo maximo
 * diario de una zona turistica antes de confirmar el informe de visita.
 * Caja Blanca: CB-08 (cupo disponible), CB-09 (cupo agotado -> excepcion).
 */
@Service
public class AforoService {

    /** Dias hacia adelante que se exploran al sugerir una fecha alternativa (CU-08). */
    private static final int DIAS_SUGERENCIA_ALTERNATIVA = 30;

    private final ControlAforoRepository controlAforoRepository;

    public AforoService(ControlAforoRepository controlAforoRepository) {
        this.controlAforoRepository = controlAforoRepository;
    }

    /**
     * CB-08/CB-09: si la zona no tiene ZonCupoMaximoDiario configurado, no
     * aplica la validacion (RF-16). Si lo tiene, incrementa el contador de
     * forma atomica cuando hay cupo disponible; si ya se alcanzo el
     * maximo, rechaza la operacion con AforoCompletoException, incluyendo
     * en el mensaje una fecha alternativa con cupo libre.
     *
     * El incremento se delega en un UPDATE ... SET AfoCupoUtilizado =
     * AfoCupoUtilizado + 1 con el limite verificado en el mismo WHERE
     * (RNF-08 / seccion 6.3), en lugar de leer, sumar y volver a guardar
     * desde la aplicacion, que dejaria una ventana de condicion de carrera.
     */
    @Transactional
    public boolean validarAforoDisponible(ZonaTuristica zona, LocalDate fecha) {
        Integer cupoMaximo = zona.getCupoMaximoDiario();
        if (cupoMaximo == null) {
            return true;
        }

        asegurarContador(zona, fecha);

        int filasActualizadas = controlAforoRepository
                .incrementarCupoUtilizado(zona.getId(), fecha, cupoMaximo);

        if (filasActualizadas == 0) {
            throw new AforoCompletoException(
                    "El aforo de la zona para la fecha seleccionada ya fue alcanzado"
                            + sugerirFechaAlternativa(zona, fecha));
        }
        return true;
    }

    /**
     * Crea el contador del dia si aun no existe. La restriccion UNIQUE
     * (AfoIdZona, AfoFecha) garantiza que, si dos peticiones simultaneas
     * intentan crearlo, solo una lo consiga; la otra reutiliza el existente.
     */
    private void asegurarContador(ZonaTuristica zona, LocalDate fecha) {
        if (controlAforoRepository.findByZona_IdAndFecha(zona.getId(), fecha).isPresent()) {
            return;
        }
        ControlAforo nuevo = new ControlAforo();
        nuevo.setZona(zona);
        nuevo.setFecha(fecha);
        nuevo.setCupoUtilizado(0);
        try {
            controlAforoRepository.saveAndFlush(nuevo);
        } catch (DataIntegrityViolationException ex) {
            // Otro hilo lo creo primero: el contador ya existe y sirve igual.
        }
    }

    /**
     * CU-08: primera fecha posterior con cupo libre. Se limita la busqueda a
     * DIAS_SUGERENCIA_ALTERNATIVA dias; si no encuentra ninguna, devuelve
     * cadena vacia y el mensaje queda solo con el rechazo.
     */
    private String sugerirFechaAlternativa(ZonaTuristica zona, LocalDate fecha) {
        return buscarFechaAlternativa(zona, fecha)
                .map(alternativa -> ". Fecha alternativa sugerida: " + alternativa)
                .orElse("");
    }

    /** Expuesto para que la vista pueda ofrecer la fecha alternativa (CN-09). */
    @Transactional(readOnly = true)
    public Optional<LocalDate> buscarFechaAlternativa(ZonaTuristica zona, LocalDate fecha) {
        Integer cupoMaximo = zona.getCupoMaximoDiario();
        if (cupoMaximo == null) {
            return Optional.empty();
        }
        for (int dia = 1; dia <= DIAS_SUGERENCIA_ALTERNATIVA; dia++) {
            LocalDate candidata = fecha.plusDays(dia);
            int usado = controlAforoRepository.findByZona_IdAndFecha(zona.getId(), candidata)
                    .map(ControlAforo::getCupoUtilizado)
                    .orElse(0);
            if (usado < cupoMaximo) {
                return Optional.of(candidata);
            }
        }
        return Optional.empty();
    }

    /** Cupo restante de la zona para la fecha, o vacio si la zona no controla aforo. */
    @Transactional(readOnly = true)
    public Optional<Integer> consultarCupoDisponible(ZonaTuristica zona, LocalDate fecha) {
        Integer cupoMaximo = zona.getCupoMaximoDiario();
        if (cupoMaximo == null) {
            return Optional.empty();
        }
        int usado = controlAforoRepository.findByZona_IdAndFecha(zona.getId(), fecha)
                .map(ControlAforo::getCupoUtilizado)
                .orElse(0);
        return Optional.of(Math.max(cupoMaximo - usado, 0));
    }
}
