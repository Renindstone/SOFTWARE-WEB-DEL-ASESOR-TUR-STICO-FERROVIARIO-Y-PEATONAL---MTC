package com.turismo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "servicio_tren")
public class ServicioTren {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SerIdServicio")
    private Integer serIdServicio;

    @Column(name = "SerHorarioSalida", nullable = false)
    private LocalTime serHorarioSalida;

    @Column(name = "SerHorarioLlegada", nullable = false)
    private LocalTime serHorarioLlegada;

    @Column(name = "SerTiempoTransitoMin", nullable = false)
    private Integer serTiempoTransitoMin;

    @Column(name = "SerTarifa", precision = 7, scale = 2, nullable = false)
    private BigDecimal serTarifa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SerIdEstacionOrigen", nullable = false)
    private Estacion estacionOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SerIdEstacionDestino", nullable = false)
    private Estacion estacionDestino;

    public ServicioTren() {
    }

    public Integer getSerIdServicio() {
        return serIdServicio;
    }

    public void setSerIdServicio(Integer serIdServicio) {
        this.serIdServicio = serIdServicio;
    }

    public LocalTime getSerHorarioSalida() {
        return serHorarioSalida;
    }

    public void setSerHorarioSalida(LocalTime serHorarioSalida) {
        this.serHorarioSalida = serHorarioSalida;
    }

    public LocalTime getSerHorarioLlegada() {
        return serHorarioLlegada;
    }

    public void setSerHorarioLlegada(LocalTime serHorarioLlegada) {
        this.serHorarioLlegada = serHorarioLlegada;
    }

    public Integer getSerTiempoTransitoMin() {
        return serTiempoTransitoMin;
    }

    public void setSerTiempoTransitoMin(Integer serTiempoTransitoMin) {
        this.serTiempoTransitoMin = serTiempoTransitoMin;
    }

    public BigDecimal getSerTarifa() {
        return serTarifa;
    }

    public void setSerTarifa(BigDecimal serTarifa) {
        this.serTarifa = serTarifa;
    }

    public Estacion getEstacionOrigen() {
        return estacionOrigen;
    }

    public void setEstacionOrigen(Estacion estacionOrigen) {
        this.estacionOrigen = estacionOrigen;
    }

    public Estacion getEstacionDestino() {
        return estacionDestino;
    }

    public void setEstacionDestino(Estacion estacionDestino) {
        this.estacionDestino = estacionDestino;
    }
}
