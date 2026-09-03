package com.turismo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "informe_planificacion")
public class InformePlanificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "InfIdInforme")
    private Integer infIdInforme;

    @Column(name = "InfCodigo", length = 15, nullable = false, unique = true)
    private String infCodigo;

    @Column(name = "InfFechaEmision", nullable = false)
    private LocalDateTime infFechaEmision = LocalDateTime.now();

    @Column(name = "InfFechaVisita", nullable = false)
    private LocalDate infFechaVisita;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "InfIdUsuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "InfIdRuta", nullable = false)
    private RutaPeatonal ruta;

    @Column(name = "InfTotalEstimado", precision = 7, scale = 2, nullable = false)
    private BigDecimal infTotalEstimado;

    public InformePlanificacion() {
    }

    public Integer getInfIdInforme() {
        return infIdInforme;
    }

    public void setInfIdInforme(Integer infIdInforme) {
        this.infIdInforme = infIdInforme;
    }

    public String getInfCodigo() {
        return infCodigo;
    }

    public void setInfCodigo(String infCodigo) {
        this.infCodigo = infCodigo;
    }

    public LocalDateTime getInfFechaEmision() {
        return infFechaEmision;
    }

    public void setInfFechaEmision(LocalDateTime infFechaEmision) {
        this.infFechaEmision = infFechaEmision;
    }

    public LocalDate getInfFechaVisita() {
        return infFechaVisita;
    }

    public void setInfFechaVisita(LocalDate infFechaVisita) {
        this.infFechaVisita = infFechaVisita;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public RutaPeatonal getRuta() {
        return ruta;
    }

    public void setRuta(RutaPeatonal ruta) {
        this.ruta = ruta;
    }

    public BigDecimal getInfTotalEstimado() {
        return infTotalEstimado;
    }

    public void setInfTotalEstimado(BigDecimal infTotalEstimado) {
        this.infTotalEstimado = infTotalEstimado;
    }
}
