package com.turismo.integration.senamhi;

import com.turismo.integration.senamhi.dto.PrevisionClimaSenamhiDTO;
import com.turismo.exception.FeedInvalidoException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;

/**
 * Adaptador hacia el servicio/API del SENAMHI: previsiones climaticas por
 * estacion/coordenadas (RF-14). Consumido por SincronizacionSenamhiJob
 * (RNF-03).
 */
@Component
public class SenamhiClient {

    private final RestClient senamhiRestClient;

    public SenamhiClient(RestClient senamhiRestClient) {
        this.senamhiRestClient = senamhiRestClient;
    }

    /** Obtiene el pronostico diario publicado por SENAMHI para todas las estaciones. */
    public List<PrevisionClimaSenamhiDTO> obtenerPrevisiones() {
        // TODO: senamhiRestClient.get().uri("/previsiones")...
        return List.of();
    }

    /**
     * Caso de prueba CB-03/CB-04: valida el feed recibido de SENAMHI y lo
     * descarta si la probabilidad de lluvia esta fuera del rango 0-100.
     * Cuando es valido, delega en ClimaService el insert/update de
     * PrevisionClima (RF-14).
     */
    public void procesarFeedSenamhi(PrevisionClimaSenamhiDTO feed) {
        BigDecimal probabilidad = feed.getProbabilidadLluvia();
        if (probabilidad == null
                || probabilidad.compareTo(BigDecimal.ZERO) < 0
                || probabilidad.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new FeedInvalidoException("Probabilidad de lluvia fuera de rango (0-100)");
        }
        // TODO: delegar en ClimaService el insert/update de PrevisionClima.
    }
}
