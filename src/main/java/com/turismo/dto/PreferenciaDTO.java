package com.turismo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Datos ingresados por el turista en el formulario de preferencias
 * (RF-01) y en la seleccion de estacion de partida (RF-02).
 */
public class PreferenciaDTO {

    /** Ids de TipoTurismo seleccionados: Aventura, Naturaleza, Cultural/Historico. */
    @NotNull
    private List<Integer> idsTipoTurismo;

    @NotNull
    private Integer tiempoDisponibleMin;

    /** Baja, Media, Alta. */
    @NotBlank
    private String dificultad;

    @NotNull
    private Integer idEstacionOrigen;

    public List<Integer> getIdsTipoTurismo() {
        return idsTipoTurismo;
    }

    public void setIdsTipoTurismo(List<Integer> idsTipoTurismo) {
        this.idsTipoTurismo = idsTipoTurismo;
    }

    public Integer getTiempoDisponibleMin() {
        return tiempoDisponibleMin;
    }

    public void setTiempoDisponibleMin(Integer tiempoDisponibleMin) {
        this.tiempoDisponibleMin = tiempoDisponibleMin;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }

    public Integer getIdEstacionOrigen() {
        return idEstacionOrigen;
    }

    public void setIdEstacionOrigen(Integer idEstacionOrigen) {
        this.idEstacionOrigen = idEstacionOrigen;
    }
}
