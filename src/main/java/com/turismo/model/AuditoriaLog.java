package com.turismo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * No mantiene FK fisica hacia las demas tablas (ver 6.3 del documento):
 * referencia el nombre de tabla e identificador afectado como texto, para
 * no acoplarse a la estructura de cada entidad auditada.
 */
@Entity
@Table(name = "auditoria_log")
@Data // getters y setters
@AllArgsConstructor // constructor con todos los argumentos
@NoArgsConstructor // constructor sin argumentos
public class AuditoriaLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AudIdLog")
    private Integer id;

    @Column(name = "AudFecha", nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Column(name = "AudUsuario", length = 50, nullable = false)
    private String usuario;

    /** Valores permitidos: INSERT, UPDATE, DELETE, SYNC. */
    @Column(name = "AudOperacion", length = 20, nullable = false)
    private String operacion;

    @Column(name = "AudTablaAfectada", length = 50, nullable = false)
    private String tablaAfectada;

    @Column(name = "AudValorAnterior", length = 500)
    private String valorAnterior;

    @Column(name = "AudValorNuevo", length = 500)
    private String valorNuevo;

}
