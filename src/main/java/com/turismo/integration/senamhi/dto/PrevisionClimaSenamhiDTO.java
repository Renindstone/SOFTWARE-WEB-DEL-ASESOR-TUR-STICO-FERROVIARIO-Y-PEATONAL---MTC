package com.turismo.integration.senamhi.dto;

import java.math.BigDecimal;

/** Representa el registro de prevision climatica tal como lo entrega el feed de SENAMHI. */
public class PrevisionClimaSenamhiDTO {
    private String codigoEstacion;
    private String fecha;
    private BigDecimal temperaturaC;
    private BigDecimal probabilidadLluvia;
    private String estadoClima;

    public String getCodigoEstacion() { return codigoEstacion; }
    public void setCodigoEstacion(String codigoEstacion) { this.codigoEstacion = codigoEstacion; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public BigDecimal getTemperaturaC() { return temperaturaC; }
    public void setTemperaturaC(BigDecimal temperaturaC) { this.temperaturaC = temperaturaC; }
    public BigDecimal getProbabilidadLluvia() { return probabilidadLluvia; }
    public void setProbabilidadLluvia(BigDecimal probabilidadLluvia) { this.probabilidadLluvia = probabilidadLluvia; }
    public String getEstadoClima() { return estadoClima; }
    public void setEstadoClima(String estadoClima) { this.estadoClima = estadoClima; }
}
