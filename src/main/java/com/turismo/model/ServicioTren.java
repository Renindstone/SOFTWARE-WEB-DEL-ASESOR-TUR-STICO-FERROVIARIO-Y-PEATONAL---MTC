package com.turismo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "servicio_tren")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServicioTren {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SerIdServicio")
    private Integer id;

    @Column(name = "SerHorarioSalida", nullable = false)
    private LocalTime horarioSalida;

    @Column(name = "SerHorarioLlegada", nullable = false)
    private LocalTime horarioLlegada;

    @Column(name = "SerTiempoTransitoMin", nullable = false)
    private Integer tiempoTransitoMin;

    @Column(name = "SerTarifa", precision = 7, scale = 2, nullable = false)
    private BigDecimal tarifa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SerIdEstacionOrigen", nullable = false)
    private Estacion estacionOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SerIdEstacionDestino", nullable = false)
    private Estacion estacionDestino;

}
