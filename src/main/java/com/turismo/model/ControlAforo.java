package com.turismo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "control_aforo",
        uniqueConstraints = @UniqueConstraint(columnNames = {"AfoIdZona", "AfoFecha"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControlAforo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AfoIdAforo")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AfoIdZona", nullable = false)
    private ZonaTuristica zona;

    @Column(name = "AfoFecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "AfoCupoUtilizado", nullable = false)
    private Integer cupoUtilizado = 0;
}
