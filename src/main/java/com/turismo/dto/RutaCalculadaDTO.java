package com.turismo.dto;

import java.math.BigDecimal;

/**
 * Resultado del motor de rutas (RF-04/RF-05): circuito peatonal de ida y
 * vuelta calculado con la formula de Haversine (ver 5.1 del documento).
 */
public class RutaCalculadaDTO {

    private BigDecimal distanciaKm;
    private Integer tiempoEstimadoMin;
    /** Baja, Media, Alta. */
    private String dificultad;
    private Boolean esIdaVuelta = Boolean.TRUE;

    public BigDecimal getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(BigDecimal distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public Integer getTiempoEstimadoMin() {
        return tiempoEstimadoMin;
    }

    public void setTiempoEstimadoMin(Integer tiempoEstimadoMin) {
        this.tiempoEstimadoMin = tiempoEstimadoMin;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public Boolean getEsIdaVuelta() {
        return esIdaVuelta;
    }

    public void setEsIdaVuelta(Boolean esIdaVuelta) {
        this.esIdaVuelta = esIdaVuelta;
    }
}
