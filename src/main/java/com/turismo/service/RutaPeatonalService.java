package com.turismo.service;

import com.turismo.dto.RutaCalculadaDTO;
import com.turismo.exception.EstacionInactivaException;
import com.turismo.exception.RutaInvalidaException;
import com.turismo.model.Estacion;
import com.turismo.model.RutaPeatonal;
import com.turismo.model.ZonaTuristica;
import com.turismo.repository.RutaPeatonalRepository;
import com.turismo.util.HaversineCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
     * origen y la zona turistica de destino, a partir de las coordenadas de
     * ambos puntos (seccion 5.1). Si la distancia de ida es cero, lanza
     * RutaInvalidaException (no existe circuito caminable).
     */
    public RutaCalculadaDTO calcularRutaPeatonalIdaVuelta(Estacion origen, ZonaTuristica destino) {
        validarOrigenActivo(origen);

        BigDecimal distanciaIda = HaversineCalculator.calcularDistanciaKm(
                origen.getLatitud(), origen.getLongitud(),
                destino.getLatitud(), destino.getLongitud());

        if (distanciaIda.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RutaInvalidaException(
                    "La zona turística debe encontrarse a una distancia caminable mayor a cero");
        }

        BigDecimal distanciaIdaVuelta = distanciaIda.multiply(BigDecimal.valueOf(2));

        RutaCalculadaDTO dto = new RutaCalculadaDTO();
        dto.setNombre("Circuito " + origen.getNombre() + " - " + destino.getNombre());
        dto.setDistanciaKm(distanciaIdaVuelta);
        dto.setTiempoEstimadoMin(calcularTiempoEstimadoMin(distanciaIdaVuelta));
        dto.setDificultad(calcularDificultad(distanciaIdaVuelta));
        dto.setEsIdaVuelta(Boolean.TRUE);
        return dto;
    }

    /**
     * RF-02: una estacion marcada como Inactiva no puede usarse como punto de
     * partida, aunque se alcance el calculo por URL directa saltando el
     * selector del formulario.
     */
    private void validarOrigenActivo(Estacion origen) {
        if (!"Activa".equalsIgnoreCase(origen.getEstado())) {
            throw new EstacionInactivaException(
                    "La estación " + origen.getNombre() + " está inactiva y no puede usarse como punto de partida");
        }
    }

    /**
     * RNF-04: persiste el circuito calculado como RutaPeatonal, reutilizando
     * la ruta ya registrada para el mismo par estacion-zona en vez de
     * duplicarla en cada consulta. La entidad exige RutEsIdaVuelta = TRUE
     * (regla de negocio del caso, tambien validada por CHECK en la BD).
     */
    @Transactional
    public RutaPeatonal obtenerOCrearRuta(Estacion origen, ZonaTuristica destino, RutaCalculadaDTO calculo) {
        RutaPeatonal ruta = rutaPeatonalRepository
                .findFirstByEstacionOrigen_IdAndZonaDestino_Id(origen.getId(), destino.getId())
                .orElseGet(RutaPeatonal::new);

        ruta.setNombre(recortar(calculo.getNombre(), 100));
        if (ruta.getDescripcion() == null) {
            ruta.setDescripcion(recortar("Circuito peatonal de ida y vuelta entre "
                    + origen.getNombre() + " y " + destino.getNombre() + ".", 500));
        }
        ruta.setDistanciaKm(calculo.getDistanciaKm());
        ruta.setTiempoEstimadoMin(calculo.getTiempoEstimadoMin());
        ruta.setDificultad(calculo.getDificultad());
        ruta.setEstacionOrigen(origen);
        ruta.setZonaDestino(destino);
        ruta.setEsIdaVuelta(Boolean.TRUE);

        return rutaPeatonalRepository.save(ruta);
    }

    /** Tiempo minimo de 1 minuto: RutTiempoEstimadoMin tiene CHECK > 0. */
    private int calcularTiempoEstimadoMin(BigDecimal distanciaIdaVuelta) {
        int minutos = distanciaIdaVuelta
                .multiply(BigDecimal.valueOf(VELOCIDAD_CAMINATA_MIN_POR_KM))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
        return Math.max(minutos, 1);
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

    private String recortar(String texto, int maximo) {
        return texto != null && texto.length() > maximo ? texto.substring(0, maximo) : texto;
    }

    public RutaPeatonal guardar(RutaPeatonal ruta) {
        return rutaPeatonalRepository.save(ruta);
    }
}
