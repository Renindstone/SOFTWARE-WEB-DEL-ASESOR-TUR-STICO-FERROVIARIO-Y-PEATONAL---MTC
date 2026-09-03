package com.turismo.model;

import jakarta.persistence.*;

/**
 * Entidad intermedia que resuelve la relacion N:M entre ZonaTuristica y
 * TipoTurismo (una zona puede pertenecer simultaneamente a mas de una
 * categoria turistica). Ver seccion 6.1 y 6.4 de ProyectoFinalSOftware.docx.
 */
@Entity
@Table(name = "zona_tipo_turismo",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ZtiIdZonaTuristica", "ZtiIdTipoTurismo"}))
public class ZonaTipoTurismo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ZtiIdZonaTipo")
    private Integer ztiIdZonaTipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ZtiIdZonaTuristica", nullable = false)
    private ZonaTuristica zonaTuristica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ZtiIdTipoTurismo", nullable = false)
    private TipoTurismo tipoTurismo;

    public ZonaTipoTurismo() {
    }

    public Integer getZtiIdZonaTipo() {
        return ztiIdZonaTipo;
    }

    public void setZtiIdZonaTipo(Integer ztiIdZonaTipo) {
        this.ztiIdZonaTipo = ztiIdZonaTipo;
    }

    public ZonaTuristica getZonaTuristica() {
        return zonaTuristica;
    }

    public void setZonaTuristica(ZonaTuristica zonaTuristica) {
        this.zonaTuristica = zonaTuristica;
    }

    public TipoTurismo getTipoTurismo() {
        return tipoTurismo;
    }

    public void setTipoTurismo(TipoTurismo tipoTurismo) {
        this.tipoTurismo = tipoTurismo;
    }
}
