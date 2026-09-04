package com.turismo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad intermedia que resuelve la relacion N:M entre ZonaTuristica y
 * TipoTurismo (una zona puede pertenecer simultaneamente a mas de una
 * categoria turistica). Ver seccion 6.1 y 6.4 de ProyectoFinalSOftware.docx.
 */
@Entity
@Table(name = "zona_tipo_turismo",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ZtiIdZonaTuristica", "ZtiIdTipoTurismo"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZonaTipoTurismo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ZtiIdZonaTipo")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ZtiIdZonaTuristica", nullable = false)
    private ZonaTuristica zonaTuristica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ZtiIdTipoTurismo", nullable = false)
    private TipoTurismo tipoTurismo;

}
