package com.turismo.integration.scheduler;

import com.turismo.integration.perurail.PeruRailClient;
import com.turismo.service.AuditoriaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * RF-13/RNF-03: sincroniza periodicamente estaciones, horarios y tarifas
 * desde PeruRail, y registra la sincronizacion en AuditoriaLog (RF-15).
 */
@Component
public class SincronizacionPeruRailJob {

    private final PeruRailClient peruRailClient;
    private final AuditoriaService auditoriaService;

    public SincronizacionPeruRailJob(PeruRailClient peruRailClient, AuditoriaService auditoriaService) {
        this.peruRailClient = peruRailClient;
        this.auditoriaService = auditoriaService;
    }

    @Scheduled(cron = "${integracion.perurail.cron:0 0 3 * * *}")
    public void sincronizar() {
        // TODO: obtener estaciones/servicios de PeruRailClient, persistir y auditar (SYNC).
    }
}
