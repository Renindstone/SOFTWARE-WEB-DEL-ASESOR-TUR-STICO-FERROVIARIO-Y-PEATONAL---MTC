package com.turismo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "estacion")
public class Estacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EstIdEstacion")
    private Integer estIdEstacion;

    @Column(name = "EstCodigo", length = 10, nullable = false, unique = true)
    private String estCodigo;

    @Column(name = "EstNombre", length = 80, nullable = false)
    private String estNombre;

    @Column(name = "EstLatitud", precision = 9, scale = 6, nullable = false)
    private BigDecimal estLatitud;

    @Column(name = "EstLongitud", precision = 9, scale = 6, nullable = false)
    private BigDecimal estLongitud;

    @Column(name = "EstAltitud", precision = 6, scale = 2)
    private BigDecimal estAltitud;

    @Column(name = "EstCiudad", length = 50, nullable = false)
    private String estCiudad;

    @Column(name = "EstEstado", length = 10, nullable = false)
    private String estEstado = "Activa";

    public Estacion() {
    }

    public Integer getEstIdEstacion() {
        return estIdEstacion;
    }

    public void setEstIdEstacion(Integer estIdEstacion) {
        this.estIdEstacion = estIdEstacion;
    }

    public String getEstCodigo() {
        return estCodigo;
    }

    public void setEstCodigo(String estCodigo) {
        this.estCodigo = estCodigo;
    }

    public String getEstNombre() {
        return estNombre;
    }

    public void setEstNombre(String estNombre) {
        this.estNombre = estNombre;
    }

    public BigDecimal getEstLatitud() {
        return estLatitud;
    }

    public void setEstLatitud(BigDecimal estLatitud) {
        this.estLatitud = estLatitud;
    }

    public BigDecimal getEstLongitud() {
        return estLongitud;
    }

    public void setEstLongitud(BigDecimal estLongitud) {
        this.estLongitud = estLongitud;
    }

    public BigDecimal getEstAltitud() {
        return estAltitud;
    }

    public void setEstAltitud(BigDecimal estAltitud) {
        this.estAltitud = estAltitud;
    }

    public String getEstCiudad() {
        return estCiudad;
    }

    public void setEstCiudad(String estCiudad) {
        this.estCiudad = estCiudad;
    }

    public String getEstEstado() {
        return estEstado;
    }

    public void setEstEstado(String estEstado) {
        this.estEstado = estEstado;
    }
}
