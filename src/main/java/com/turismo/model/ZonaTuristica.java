package com.turismo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "zona_turistica")
public class ZonaTuristica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ZonIdZona")
    private Integer zonIdZona;

    @Column(name = "ZonNombre", length = 100, nullable = false)
    private String zonNombre;

    @Column(name = "ZonDescripcion", length = 500)
    private String zonDescripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ZonIdEstacionCercana", nullable = false)
    private Estacion estacionCercana;

    @Column(name = "ZonCostoAprox", precision = 7, scale = 2)
    private BigDecimal zonCostoAprox;

    @Column(name = "ZonCupoMaximoDiario")
    private Integer zonCupoMaximoDiario;

    @Column(name = "ZonEstado", length = 10, nullable = false)
    private String zonEstado = "Activa";

    @OneToMany(mappedBy = "zonaTuristica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ZonaTipoTurismo> tiposTurismo = new ArrayList<>();

    public ZonaTuristica() {
    }

    public Integer getZonIdZona() {
        return zonIdZona;
    }

    public void setZonIdZona(Integer zonIdZona) {
        this.zonIdZona = zonIdZona;
    }

    public String getZonNombre() {
        return zonNombre;
    }

    public void setZonNombre(String zonNombre) {
        this.zonNombre = zonNombre;
    }

    public String getZonDescripcion() {
        return zonDescripcion;
    }

    public void setZonDescripcion(String zonDescripcion) {
        this.zonDescripcion = zonDescripcion;
    }

    public Estacion getEstacionCercana() {
        return estacionCercana;
    }

    public void setEstacionCercana(Estacion estacionCercana) {
        this.estacionCercana = estacionCercana;
    }

    public BigDecimal getZonCostoAprox() {
        return zonCostoAprox;
    }

    public void setZonCostoAprox(BigDecimal zonCostoAprox) {
        this.zonCostoAprox = zonCostoAprox;
    }

    public Integer getZonCupoMaximoDiario() {
        return zonCupoMaximoDiario;
    }

    public void setZonCupoMaximoDiario(Integer zonCupoMaximoDiario) {
        this.zonCupoMaximoDiario = zonCupoMaximoDiario;
    }

    public String getZonEstado() {
        return zonEstado;
    }

    public void setZonEstado(String zonEstado) {
        this.zonEstado = zonEstado;
    }

    public List<ZonaTipoTurismo> getTiposTurismo() {
        return tiposTurismo;
    }

    public void setTiposTurismo(List<ZonaTipoTurismo> tiposTurismo) {
        this.tiposTurismo = tiposTurismo;
    }
}
