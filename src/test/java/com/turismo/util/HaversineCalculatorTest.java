package com.turismo.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Formula de Haversine usada por RutaPeatonalService (seccion 5.1). Los
 * valores esperados coinciden con fn_distancia_haversine de
 * init/03_consultas.sql, para que la aplicacion y la base de datos reporten
 * la misma distancia sobre los mismos datos.
 */
class HaversineCalculatorTest {

    @Test
    void calculaLaDistanciaEntreLaEstacionYLaZonaTuristica() {
        // Estacion Ollantaytambo -> Conjunto Arqueologico de Ollantaytambo.
        BigDecimal distancia = HaversineCalculator.calcularDistanciaKm(
                new BigDecimal("-13.258600"), new BigDecimal("-72.265000"),
                new BigDecimal("-13.254466"), new BigDecimal("-72.268563"));

        assertThat(distancia).isEqualByComparingTo("0.60");
    }

    @Test
    void devuelveCeroCuandoAmbasCoordenadasCoinciden() {
        BigDecimal distancia = HaversineCalculator.calcularDistanciaKm(
                new BigDecimal("-13.154700"), new BigDecimal("-72.525000"),
                new BigDecimal("-13.154700"), new BigDecimal("-72.525000"));

        assertThat(distancia).isEqualByComparingTo("0.00");
    }

    @Test
    void redondeaADosDecimalesComoRutDistanciaKm() {
        // Estacion Machu Picchu -> Llaqta de Machu Picchu (5.00 km de ida).
        BigDecimal distancia = HaversineCalculator.calcularDistanciaKm(
                new BigDecimal("-13.154700"), new BigDecimal("-72.525000"),
                new BigDecimal("-13.193641"), new BigDecimal("-72.548093"));

        assertThat(distancia.scale()).isEqualTo(2);
        assertThat(distancia).isEqualByComparingTo("5.00");
    }
}
