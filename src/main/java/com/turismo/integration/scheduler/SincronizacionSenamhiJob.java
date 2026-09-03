package com.turismo.integration.scheduler;

import com.turismo.integration.senamhi.SenamhiClient;
import com.turismo.service.AuditoriaService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * RF-14/RNF-03: sincroniza diariamente las previsiones climaticas del
 * SENAMHI por estacion, y registra la sincronizacion en AuditoriaLog (RF-15).
 */
@Component
public class SincronizacionSenamhiJob {

    private final SenamhiClient senamhiClient;
    private final AuditoriaService auditoriaService;

    public SincronizacionSenamhiJob(SenamhiClient senamhiClient, AuditoriaService auditoriaService) {
        this.senamhiClient = senamhiClient;
        this.auditoriaService = auditoriaService;
    }

    @Scheduled(cron = "${integracion.senamhi.cron:0 0 4 * * *}")
    public void sincronizar() {
        // TODO: obtener previsiones de SenamhiClient, procesarlas y auditar (SYNC).
    }
}
