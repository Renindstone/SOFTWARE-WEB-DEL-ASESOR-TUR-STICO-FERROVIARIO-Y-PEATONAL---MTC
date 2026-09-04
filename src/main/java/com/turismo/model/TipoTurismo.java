package com.turismo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_turismo")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoTurismo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TipIdTipoTurismo")
    private Integer id;

    @Column(name = "TipNombre", length = 30, nullable = false, unique = true)
    private String nombre;

    @Column(name = "TipDescripcion", length = 150)
    private String descripcion;

}
