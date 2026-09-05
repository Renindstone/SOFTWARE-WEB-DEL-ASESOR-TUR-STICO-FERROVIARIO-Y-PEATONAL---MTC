package com.turismo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Datos ingresados por el turista en el formulario de preferencias
 * (RF-01) y en la seleccion de estacion de partida (RF-02).
 */
public class PreferenciaDTO {

    /** Ids de TipoTurismo seleccionados de la tabla parametrica (RNF-06). */
    @NotEmpty(message = "Debe seleccionar al menos un tipo de turismo")
    private List<Integer> idsTipoTurismo;

    @NotNull(message = "Indique el tiempo disponible para la caminata")
    @Min(value = 1, message = "El tiempo disponible debe ser mayor a cero")
    private Integer tiempoDisponibleMin;

    /** Baja, Media, Alta (RutDificultad del diccionario de datos). */
    @NotBlank(message = "Seleccione el nivel de dificultad")
    private String dificultad;

    @NotNull(message = "Seleccione la estación ferroviaria de partida")
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
