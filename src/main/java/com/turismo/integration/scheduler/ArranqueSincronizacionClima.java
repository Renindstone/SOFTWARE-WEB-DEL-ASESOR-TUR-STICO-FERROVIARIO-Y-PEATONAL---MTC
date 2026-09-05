package com.turismo.integration.scheduler;

import com.turismo.repository.PrevisionClimaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * RNF-03: la tarea programada del SENAMHI corre de madrugada. Sin esto, una
 * aplicacion que arranca a media manana se queda sin pronostico hasta el dia
 * siguiente y el turista ve "sin pronostico publicado" en el detalle de la
 * ruta y en el informe consolidado.
 *
 * Al terminar el arranque se comprueba si ya hay previsiones para hoy y, si
 * faltan, se lanza la misma sincronizacion del job. La comprobacion evita
 * repetir el trabajo en cada reinicio; de todos modos la carga es idempotente
 * (PrevisionClima tiene UNIQUE por estacion y fecha, y ClimaService hace
 * insert o update).
 */
@Component
@ConditionalOnProperty(name = "integracion.senamhi.sincronizar-al-iniciar",
        havingValue = "true", matchIfMissing = true)
public class ArranqueSincronizacionClima {

    private static final Logger LOG = LoggerFactory.getLogger(ArranqueSincronizacionClima.class);

    private final PrevisionClimaRepository previsionClimaRepository;
    private final SincronizacionSenamhiJob sincronizacionSenamhiJob;

    public ArranqueSincronizacionClima(PrevisionClimaRepository previsionClimaRepository,
                                        SincronizacionSenamhiJob sincronizacionSenamhiJob) {
        this.previsionClimaRepository = previsionClimaRepository;
        this.sincronizacionSenamhiJob = sincronizacionSenamhiJob;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void sincronizarSiFaltaElPronosticoDeHoy() {
        LocalDate hoy = LocalDate.now();
        if (previsionClimaRepository.existsByFecha(hoy)) {
            LOG.info("El pronóstico del {} ya está cargado; no se sincroniza al iniciar", hoy);
            return;
        }

        LOG.info("No hay pronóstico para el {}; sincronizando con el SENAMHI al iniciar", hoy);
        try {
            sincronizacionSenamhiJob.sincronizar();
        } catch (RuntimeException ex) {
            // El arranque no debe fallar porque el servicio externo no responda:
            // la tarea programada volvera a intentarlo en su proxima ejecucion.
            LOG.warn("No se pudo sincronizar el clima al iniciar: {}", ex.getMessage());
        }
    }
}
