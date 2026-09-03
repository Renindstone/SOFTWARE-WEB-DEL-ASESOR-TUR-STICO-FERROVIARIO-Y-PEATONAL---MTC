package com.turismo.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calcula la distancia en linea recta entre dos coordenadas geograficas
 * usando la formula de Haversine. RutaPeatonalService la usa para estimar
 * la distancia de ida entre la estacion de origen y la zona turistica, y
 * la multiplica por dos para representar el circuito de ida y vuelta
 * (ver nota tecnica de la seccion 5.1 del documento).
 */
public final class HaversineCalculator {

    private static final double RADIO_TIERRA_KM = 6371.0;

    private HaversineCalculator() {
    }

    public static BigDecimal calcularDistanciaKm(BigDecimal lat1, BigDecimal lon1,
                                                   BigDecimal lat2, BigDecimal lon2) {
        double rLat1 = Math.toRadians(lat1.doubleValue());
        double rLat2 = Math.toRadians(lat2.doubleValue());
        double deltaLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double deltaLon = Math.toRadians(lon2.doubleValue() - lon1.doubleValue());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(rLat1) * Math.cos(rLat2)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanciaKm = RADIO_TIERRA_KM * c;

        return BigDecimal.valueOf(distanciaKm).setScale(2, RoundingMode.HALF_UP);
    }
}
