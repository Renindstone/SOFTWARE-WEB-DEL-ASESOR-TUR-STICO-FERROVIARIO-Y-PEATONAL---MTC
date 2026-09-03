package com.turismo.integration.perurail;

import com.turismo.integration.perurail.dto.EstacionPeruRailDTO;
import com.turismo.integration.perurail.dto.ServicioTrenPeruRailDTO;
import com.turismo.exception.TarifaInvalidaException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;

/**
 * Adaptador hacia el servicio/API de PeruRail: catalogo de estaciones,
 * horarios, tiempos de transito y tarifas (RF-13). Consumido por
 * SincronizacionPeruRailJob (RNF-03).
 */
@Component
public class PeruRailClient {

    private final RestClient peruRailRestClient;

    public PeruRailClient(RestClient peruRailRestClient) {
        this.peruRailRestClient = peruRailRestClient;
    }

    /** Obtiene el catalogo de estaciones activas publicado por PeruRail. */
    public List<EstacionPeruRailDTO> obtenerEstaciones() {
        // TODO: peruRailRestClient.get().uri("/estaciones")...
        return List.of();
    }

    /** Obtiene horarios y tarifas de los servicios de tren disponibles. */
    public List<ServicioTrenPeruRailDTO> obtenerServicios() {
        // TODO: peruRailRestClient.get().uri("/servicios")...
        return List.of();
    }

    /**
     * Caso de prueba CB-05/CB-06: valida que la tarifa recibida sea un
     * valor numerico positivo antes de habilitar su persistencia.
     */
    public boolean validarTarifaPeruRail(BigDecimal tarifa) {
        if (tarifa == null || tarifa.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TarifaInvalidaException("La tarifa debe ser un valor mayor a cero");
        }
        return true;
    }
}
