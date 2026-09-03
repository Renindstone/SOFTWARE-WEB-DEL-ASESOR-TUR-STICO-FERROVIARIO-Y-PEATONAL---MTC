package com.turismo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ruta_peatonal")
public class RutaPeatonal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RutIdRuta")
    private Integer rutIdRuta;

    @Column(name = "RutNombre", length = 100, nullable = false)
    private String rutNombre;

    @Column(name = "RutDistanciaKm", precision = 5, scale = 2, nullable = false)
    private BigDecimal rutDistanciaKm;

    @Column(name = "RutTiempoEstimadoMin", nullable = false)
    private Integer rutTiempoEstimadoMin;

    /** Valores permitidos: Baja, Media, Alta. */
    @Column(name = "RutDificultad", length = 10, nullable = false)
    private String rutDificultad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RutIdEstacionOrigen", nullable = false)
    private Estacion estacionOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RutIdZonaDestino", nullable = false)
    private ZonaTuristica zonaDestino;

    @Column(name = "RutEsIdaVuelta", nullable = false)
    private Boolean rutEsIdaVuelta = Boolean.TRUE;

    public RutaPeatonal() {
    }

    public Integer getRutIdRuta() {
        return rutIdRuta;
    }

    public void setRutIdRuta(Integer rutIdRuta) {
        this.rutIdRuta = rutIdRuta;
    }

    public String getRutNombre() {
        return rutNombre;
    }

    public void setRutNombre(String rutNombre) {
        this.rutNombre = rutNombre;
    }

    public BigDecimal getRutDistanciaKm() {
        return rutDistanciaKm;
    }

    public void setRutDistanciaKm(BigDecimal rutDistanciaKm) {
        this.rutDistanciaKm = rutDistanciaKm;
    }

    public Integer getRutTiempoEstimadoMin() {
        return rutTiempoEstimadoMin;
    }

    public void setRutTiempoEstimadoMin(Integer rutTiempoEstimadoMin) {
        this.rutTiempoEstimadoMin = rutTiempoEstimadoMin;
    }

    public String getRutDificultad() {
        return rutDificultad;
    }

    public void setRutDificultad(String rutDificultad) {
        this.rutDificultad = rutDificultad;
    }

    public Estacion getEstacionOrigen() {
        return estacionOrigen;
    }

    public void setEstacionOrigen(Estacion estacionOrigen) {
        this.estacionOrigen = estacionOrigen;
    }

    public ZonaTuristica getZonaDestino() {
        return zonaDestino;
    }

    public void setZonaDestino(ZonaTuristica zonaDestino) {
        this.zonaDestino = zonaDestino;
    }

    public Boolean getRutEsIdaVuelta() {
        return rutEsIdaVuelta;
    }

    public void setRutEsIdaVuelta(Boolean rutEsIdaVuelta) {
        this.rutEsIdaVuelta = rutEsIdaVuelta;
    }
}
