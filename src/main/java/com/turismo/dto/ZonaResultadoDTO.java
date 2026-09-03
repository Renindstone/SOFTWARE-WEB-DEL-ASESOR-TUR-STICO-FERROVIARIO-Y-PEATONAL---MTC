package com.turismo.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Zona turistica filtrada segun las preferencias del turista (RF-03),
 * ya asociada a su(s) tipo(s) de turismo (relacion N:M ZonaTipoTurismo).
 */
public class ZonaResultadoDTO {

    private Integer idZona;
    private String nombre;
    private String descripcion;
    private List<String> tiposTurismo;
    private BigDecimal costoAproximado;
    private Integer cupoMaximoDiario;
    private Integer idEstacionCercana;

    public Integer getIdZona() {
        return idZona;
    }

    public void setIdZona(Integer idZona) {
        this.idZona = idZona;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<String> getTiposTurismo() {
        return tiposTurismo;
    }

    public void setTiposTurismo(List<String> tiposTurismo) {
        this.tiposTurismo = tiposTurismo;
    }

    public BigDecimal getCostoAproximado() {
        return costoAproximado;
    }

    public void setCostoAproximado(BigDecimal costoAproximado) {
        this.costoAproximado = costoAproximado;
    }

    public Integer getCupoMaximoDiario() {
        return cupoMaximoDiario;
    }

    public void setCupoMaximoDiario(Integer cupoMaximoDiario) {
        this.cupoMaximoDiario = cupoMaximoDiario;
    }

    public Integer getIdEstacionCercana() {
        return idEstacionCercana;
    }

    public void setIdEstacionCercana(Integer idEstacionCercana) {
        this.idEstacionCercana = idEstacionCercana;
    }
}
