package com.turismo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "informe_planificacion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InformePlanificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "InfIdInforme")
    private Integer id;

    @Column(name = "InfCodigo", length = 15, nullable = false, unique = true)
    private String codigo;

    @Column(name = "InfFechaEmision", nullable = false)
    private LocalDateTime fechaEmision = LocalDateTime.now();

    @Column(name = "InfFechaVisita", nullable = false)
    private LocalDate fechaVisita;

    /** NULL cuando la consulta fue anonima (diccionario de datos 6.4). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "InfIdUsuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "InfIdRuta", nullable = false)
    private RutaPeatonal ruta;

    @Column(name = "InfTotalEstimado", precision = 7, scale = 2, nullable = false)
    private BigDecimal totalEstimado;

}
