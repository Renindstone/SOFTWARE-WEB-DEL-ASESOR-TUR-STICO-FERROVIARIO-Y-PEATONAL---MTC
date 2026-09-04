package com.turismo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "prevision_clima", uniqueConstraints = @UniqueConstraint(columnNames = { "CliIdEstacion", "CliFecha" }))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrevisionClima {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CliIdClima")
    private Integer id;

    @Column(name = "CliFecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "CliTemperaturaC", precision = 4, scale = 1, nullable = false)
    private BigDecimal temperaturaC;

    @Column(name = "CliProbabilidadLluvia", precision = 4, scale = 1, nullable = false)
    private BigDecimal probabilidadLluvia;

    /** Valores permitidos: Soleado, Nublado, Lluvia ligera, Tormenta. */
    @Column(name = "CliEstadoClima", length = 30, nullable = false)
    private String estadoClima;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CliIdEstacion", nullable = false)
    private Estacion estacion;

}
