package com.turismo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "control_aforo",
        uniqueConstraints = @UniqueConstraint(columnNames = {"AfoIdZona", "AfoFecha"}))
public class ControlAforo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AfoIdAforo")
    private Integer afoIdAforo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AfoIdZona", nullable = false)
    private ZonaTuristica zona;

    @Column(name = "AfoFecha", nullable = false)
    private LocalDate afoFecha;

    @Column(name = "AfoCupoUtilizado", nullable = false)
    private Integer afoCupoUtilizado = 0;

    public ControlAforo() {
    }

    public Integer getAfoIdAforo() {
        return afoIdAforo;
    }

    public void setAfoIdAforo(Integer afoIdAforo) {
        this.afoIdAforo = afoIdAforo;
    }

    public ZonaTuristica getZona() {
        return zona;
    }

    public void setZona(ZonaTuristica zona) {
        this.zona = zona;
    }

    public LocalDate getAfoFecha() {
        return afoFecha;
    }

    public void setAfoFecha(LocalDate afoFecha) {
        this.afoFecha = afoFecha;
    }

    public Integer getAfoCupoUtilizado() {
        return afoCupoUtilizado;
    }

    public void setAfoCupoUtilizado(Integer afoCupoUtilizado) {
        this.afoCupoUtilizado = afoCupoUtilizado;
    }
}
