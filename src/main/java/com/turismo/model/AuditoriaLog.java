package com.turismo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * No mantiene FK fisica hacia las demas tablas (ver 6.3 del documento):
 * referencia el nombre de tabla e identificador afectado como texto, para
 * no acoplarse a la estructura de cada entidad auditada.
 */
@Entity
@Table(name = "auditoria_log")
public class AuditoriaLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AudIdLog")
    private Integer audIdLog;

    @Column(name = "AudFecha", nullable = false)
    private LocalDateTime audFecha = LocalDateTime.now();

    @Column(name = "AudUsuario", length = 50, nullable = false)
    private String audUsuario;

    /** Valores permitidos: INSERT, UPDATE, DELETE, SYNC. */
    @Column(name = "AudOperacion", length = 20, nullable = false)
    private String audOperacion;

    @Column(name = "AudTablaAfectada", length = 50, nullable = false)
    private String audTablaAfectada;

    @Column(name = "AudValorAnterior", length = 500)
    private String audValorAnterior;

    @Column(name = "AudValorNuevo", length = 500)
    private String audValorNuevo;

    public AuditoriaLog() {
    }

    public Integer getAudIdLog() {
        return audIdLog;
    }

    public void setAudIdLog(Integer audIdLog) {
        this.audIdLog = audIdLog;
    }

    public LocalDateTime getAudFecha() {
        return audFecha;
    }

    public void setAudFecha(LocalDateTime audFecha) {
        this.audFecha = audFecha;
    }

    public String getAudUsuario() {
        return audUsuario;
    }

    public void setAudUsuario(String audUsuario) {
        this.audUsuario = audUsuario;
    }

    public String getAudOperacion() {
        return audOperacion;
    }

    public void setAudOperacion(String audOperacion) {
        this.audOperacion = audOperacion;
    }

    public String getAudTablaAfectada() {
        return audTablaAfectada;
    }

    public void setAudTablaAfectada(String audTablaAfectada) {
        this.audTablaAfectada = audTablaAfectada;
    }

    public String getAudValorAnterior() {
        return audValorAnterior;
    }

    public void setAudValorAnterior(String audValorAnterior) {
        this.audValorAnterior = audValorAnterior;
    }

    public String getAudValorNuevo() {
        return audValorNuevo;
    }

    public void setAudValorNuevo(String audValorNuevo) {
        this.audValorNuevo = audValorNuevo;
    }
}
