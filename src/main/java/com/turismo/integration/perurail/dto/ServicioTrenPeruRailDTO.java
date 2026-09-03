package com.turismo.integration.perurail.dto;

import java.math.BigDecimal;

/** Representa el registro de horario/tarifa tal como lo entrega el feed de PeruRail. */
public class ServicioTrenPeruRailDTO {
    private String codigoEstacionOrigen;
    private String codigoEstacionDestino;
    private String horarioSalida;
    private String horarioLlegada;
    private Integer tiempoTransitoMin;
    private BigDecimal tarifa;

    public String getCodigoEstacionOrigen() { return codigoEstacionOrigen; }
    public void setCodigoEstacionOrigen(String codigoEstacionOrigen) { this.codigoEstacionOrigen = codigoEstacionOrigen; }
    public String getCodigoEstacionDestino() { return codigoEstacionDestino; }
    public void setCodigoEstacionDestino(String codigoEstacionDestino) { this.codigoEstacionDestino = codigoEstacionDestino; }
    public String getHorarioSalida() { return horarioSalida; }
    public void setHorarioSalida(String horarioSalida) { this.horarioSalida = horarioSalida; }
    public String getHorarioLlegada() { return horarioLlegada; }
    public void setHorarioLlegada(String horarioLlegada) { this.horarioLlegada = horarioLlegada; }
    public Integer getTiempoTransitoMin() { return tiempoTransitoMin; }
    public void setTiempoTransitoMin(Integer tiempoTransitoMin) { this.tiempoTransitoMin = tiempoTransitoMin; }
    public BigDecimal getTarifa() { return tarifa; }
    public void setTarifa(BigDecimal tarifa) { this.tarifa = tarifa; }
}
