package com.turismo.integration.senamhi.dto;

import java.math.BigDecimal;

/** Representa el registro de prevision climatica tal como lo entrega el feed de SENAMHI. */
public class PrevisionClimaSenamhiDTO {
    private String codigoEstacion;
    private String fecha;
    private BigDecimal temperaturaMinimaC;
    private BigDecimal temperaturaMaximaC;
    private BigDecimal probabilidadLluvia;
    private String estadoClima;

    public String getCodigoEstacion() { return codigoEstacion; }
    public void setCodigoEstacion(String codigoEstacion) { this.codigoEstacion = codigoEstacion; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public BigDecimal getTemperaturaMinimaC() { return temperaturaMinimaC; }
    public void setTemperaturaMinimaC(BigDecimal temperaturaMinimaC) { this.temperaturaMinimaC = temperaturaMinimaC; }
    public BigDecimal getTemperaturaMaximaC() { return temperaturaMaximaC; }
    public void setTemperaturaMaximaC(BigDecimal temperaturaMaximaC) { this.temperaturaMaximaC = temperaturaMaximaC; }
    public BigDecimal getProbabilidadLluvia() { return probabilidadLluvia; }
    public void setProbabilidadLluvia(BigDecimal probabilidadLluvia) { this.probabilidadLluvia = probabilidadLluvia; }
    public String getEstadoClima() { return estadoClima; }
    public void setEstadoClima(String estadoClima) { this.estadoClima = estadoClima; }
}
