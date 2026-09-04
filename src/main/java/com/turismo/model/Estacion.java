package com.turismo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "estacion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Estacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EstIdEstacion")
    private Integer id;

    @Column(name = "EstCodigo", length = 10, nullable = false, unique = true)
    private String codigo;

    @Column(name = "EstNombre", length = 80, nullable = false)
    private String nombre;

    @Column(name = "EstLatitud", precision = 9, scale = 6, nullable = false)
    private BigDecimal latitud;

    @Column(name = "EstLongitud", precision = 9, scale = 6, nullable = false)
    private BigDecimal longitud;

    @Column(name = "EstAltitud", precision = 6, scale = 2)
    private BigDecimal altitud;

    @Column(name = "EstCiudad", length = 50, nullable = false)
    private String ciudad;

    @Column(name = "EstEstado", length = 10, nullable = false)
    private String estado = "Activa";
}
