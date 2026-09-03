package com.turismo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_turismo")
public class TipoTurismo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TipIdTipoTurismo")
    private Integer tipIdTipoTurismo;

    @Column(name = "TipNombre", length = 30, nullable = false, unique = true)
    private String tipNombre;

    @Column(name = "TipDescripcion", length = 150)
    private String tipDescripcion;

    public TipoTurismo() {
    }

    public Integer getTipIdTipoTurismo() {
        return tipIdTipoTurismo;
    }

    public void setTipIdTipoTurismo(Integer tipIdTipoTurismo) {
        this.tipIdTipoTurismo = tipIdTipoTurismo;
    }

    public String getTipNombre() {
        return tipNombre;
    }

    public void setTipNombre(String tipNombre) {
        this.tipNombre = tipNombre;
    }

    public String getTipDescripcion() {
        return tipDescripcion;
    }

    public void setTipDescripcion(String tipDescripcion) {
        this.tipDescripcion = tipDescripcion;
    }
}
