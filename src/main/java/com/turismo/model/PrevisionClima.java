package com.turismo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "prevision_clima",
        uniqueConstraints = @UniqueConstraint(columnNames = {"CliIdEstacion", "CliFecha"}))
public class PrevisionClima {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CliIdClima")
    private Integer cliIdClima;

    @Column(name = "CliFecha", nullable = false)
    private LocalDate cliFecha;

    @Column(name = "CliTemperaturaMinimaC", precision = 4, scale = 1, nullable = false)
    private BigDecimal cliTemperaturaMinimaC;

    @Column(name = "CliTemperaturaMaximaC", precision = 4, scale = 1, nullable = false)
    private BigDecimal cliTemperaturaMaximaC;

    @Column(name = "CliProbabilidadLluvia", precision = 4, scale = 1, nullable = false)
    private BigDecimal cliProbabilidadLluvia;

    @Column(name = "CliEstadoClima", length = 30, nullable = false)
    private String cliEstadoClima;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CliIdEstacion", nullable = false)
    private Estacion estacion;

    public PrevisionClima() {
    }

    public Integer getCliIdClima() {
        return cliIdClima;
    }

    public void setCliIdClima(Integer cliIdClima) {
        this.cliIdClima = cliIdClima;
    }

    public LocalDate getCliFecha() {
        return cliFecha;
    }

    public void setCliFecha(LocalDate cliFecha) {
        this.cliFecha = cliFecha;
    }

    public BigDecimal getCliTemperaturaMinimaC() {
        return cliTemperaturaMinimaC;
    }

    public void setCliTemperaturaMinimaC(BigDecimal cliTemperaturaMinimaC) {
        this.cliTemperaturaMinimaC = cliTemperaturaMinimaC;
    }

    public BigDecimal getCliTemperaturaMaximaC() {
        return cliTemperaturaMaximaC;
    }

    public void setCliTemperaturaMaximaC(BigDecimal cliTemperaturaMaximaC) {
        this.cliTemperaturaMaximaC = cliTemperaturaMaximaC;
    }

    public BigDecimal getCliProbabilidadLluvia() {
        return cliProbabilidadLluvia;
    }

    public void setCliProbabilidadLluvia(BigDecimal cliProbabilidadLluvia) {
        this.cliProbabilidadLluvia = cliProbabilidadLluvia;
    }

    public String getCliEstadoClima() {
        return cliEstadoClima;
    }

    public void setCliEstadoClima(String cliEstadoClima) {
        this.cliEstadoClima = cliEstadoClima;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public void setEstacion(Estacion estacion) {
        this.estacion = estacion;
    }
}
