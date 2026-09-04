package com.turismo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "zona_turistica")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZonaTuristica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ZonIdZona")
    private Integer id;

    @Column(name = "ZonNombre", length = 100, nullable = false, unique = true)
    private String nombre;

    @Column(name = "ZonDescripcion", length = 500)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ZonIdEstacionCercana", nullable = false)
    private Estacion estacionCercana;

    @Column(name = "ZonCostoAprox", precision = 7, scale = 2)
    private BigDecimal costoAprox;

    @Column(name = "ZonCupoMaximoDiario")
    private Integer cupoMaximoDiario;

    @Column(name = "ZonEstado", length = 10, nullable = false)
    private String estado = "Disponible";

    @OneToMany(mappedBy = "zonaTuristica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ZonaTipoTurismo> tiposTurismo = new ArrayList<>();

}
