package com.turismo.service;

import com.turismo.dto.RutaCalculadaDTO;
import com.turismo.exception.RutaInvalidaException;
import com.turismo.model.Estacion;
import com.turismo.model.RutaPeatonal;
import com.turismo.model.ZonaTuristica;
import com.turismo.repository.RutaPeatonalRepository;
import com.turismo.util.HaversineCalculator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * RF-04/RF-05/RNF-04: motor de calculo de la ruta peatonal de ida y
 * vuelta, usando la formula de Haversine (ver 5.1 del documento).
 * Caja Blanca: CB-01 (ruta valida), CB-02 (distancia cero -> excepcion).
 */
@Service
public class RutaPeatonalService {

    private static final BigDecimal UMBRAL_DIFICULTAD_MEDIA_KM = BigDecimal.valueOf(3);
    private static final BigDecimal UMBRAL_DIFICULTAD_ALTA_KM = BigDecimal.valueOf(6);
    private static final int VELOCIDAD_CAMINATA_MIN_POR_KM = 12;

    private final RutaPeatonalRepository rutaPeatonalRepository;

    public RutaPeatonalService(RutaPeatonalRepository rutaPeatonalRepository) {
        this.rutaPeatonalRepository = rutaPeatonalRepository;
    }

    /**
     * CB-01/CB-02: calcula el circuito de ida y vuelta entre la estacion de
     * origen y la zona turistica de destino. Si la distancia de ida es
     * cero, lanza RutaInvalidaException (no existe circuito caminable).
     */
    public RutaCalculadaDTO calcularRutaPeatonalIdaVuelta(Estacion origen, ZonaTuristica destino) {
        BigDecimal distanciaIda = HaversineCalculator.calcularDistanciaKm(
                origen.getEstLatitud(), origen.getEstLongitud(),
                destino.getEstacionCercana().getEstLatitud(), destino.getEstacionCercana().getEstLongitud());

        if (distanciaIda.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RutaInvalidaException(
                    "La zona turística debe encontrarse a una distancia caminable mayor a cero");
        }

        BigDecimal distanciaIdaVuelta = distanciaIda.multiply(BigDecimal.valueOf(2));

        RutaCalculadaDTO dto = new RutaCalculadaDTO();
        dto.setDistanciaKm(distanciaIdaVuelta);
        dto.setTiempoEstimadoMin(distanciaIdaVuelta.multiply(BigDecimal.valueOf(VELOCIDAD_CAMINATA_MIN_POR_KM)).intValue());
        dto.setDificultad(calcularDificultad(distanciaIdaVuelta));
        dto.setEsIdaVuelta(Boolean.TRUE);
        return dto;
    }

    private String calcularDificultad(BigDecimal distanciaIdaVuelta) {
        if (distanciaIdaVuelta.compareTo(UMBRAL_DIFICULTAD_ALTA_KM) > 0) {
            return "Alta";
        }
        if (distanciaIdaVuelta.compareTo(UMBRAL_DIFICULTAD_MEDIA_KM) > 0) {
            return "Media";
        }
        return "Baja";
    }

    public RutaPeatonal guardar(RutaPeatonal ruta) {
        return rutaPeatonalRepository.save(ruta);
    }
}
