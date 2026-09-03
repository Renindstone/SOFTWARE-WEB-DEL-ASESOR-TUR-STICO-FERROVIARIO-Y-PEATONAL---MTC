package com.turismo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Informe consolidado (RF-08): ruta, clima, tiempo estimado, dificultad
 * y tarifa del tren, exportable en PDF/HTML (Modulo de Informes).
 */
public class InformeConsolidadoDTO {

    private String codigo;
    private LocalDate fechaVisita;
    private String estacionOrigen;
    private String zonaDestino;
    private RutaCalculadaDTO ruta;
    private BigDecimal temperaturaMinimaC;
    private BigDecimal temperaturaMaximaC;
    private BigDecimal probabilidadLluvia;
    private String estadoClima;
    private BigDecimal tarifaTren;
    private BigDecimal totalEstimado;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDate getFechaVisita() {
        return fechaVisita;
    }

    public void setFechaVisita(LocalDate fechaVisita) {
        this.fechaVisita = fechaVisita;
    }

    public String getEstacionOrigen() {
        return estacionOrigen;
    }

    public void setEstacionOrigen(String estacionOrigen) {
        this.estacionOrigen = estacionOrigen;
    }

    public String getZonaDestino() {
        return zonaDestino;
    }

    public void setZonaDestino(String zonaDestino) {
        this.zonaDestino = zonaDestino;
    }

    public RutaCalculadaDTO getRuta() {
        return ruta;
    }

    public void setRuta(RutaCalculadaDTO ruta) {
        this.ruta = ruta;
    }

    public BigDecimal getTemperaturaMinimaC() {
        return temperaturaMinimaC;
    }

    public void setTemperaturaMinimaC(BigDecimal temperaturaMinimaC) {
        this.temperaturaMinimaC = temperaturaMinimaC;
    }

    public BigDecimal getTemperaturaMaximaC() {
        return temperaturaMaximaC;
    }

    public void setTemperaturaMaximaC(BigDecimal temperaturaMaximaC) {
        this.temperaturaMaximaC = temperaturaMaximaC;
    }

    public BigDecimal getProbabilidadLluvia() {
        return probabilidadLluvia;
    }

    public void setProbabilidadLluvia(BigDecimal probabilidadLluvia) {
        this.probabilidadLluvia = probabilidadLluvia;
    }

    public String getEstadoClima() {
        return estadoClima;
    }

    public void setEstadoClima(String estadoClima) {
        this.estadoClima = estadoClima;
    }

    public BigDecimal getTarifaTren() {
        return tarifaTren;
    }

    public void setTarifaTren(BigDecimal tarifaTren) {
        this.tarifaTren = tarifaTren;
    }

    public BigDecimal getTotalEstimado() {
        return totalEstimado;
    }

    public void setTotalEstimado(BigDecimal totalEstimado) {
        this.totalEstimado = totalEstimado;
    }
}
