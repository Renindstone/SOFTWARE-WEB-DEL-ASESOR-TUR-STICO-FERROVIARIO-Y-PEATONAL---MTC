package com.turismo.repository;

import com.turismo.model.ControlAforo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ControlAforoRepository extends JpaRepository<ControlAforo, Integer> {

    Optional<ControlAforo> findByZona_IdAndFecha(Integer idZona, LocalDate fecha);

    /**
     * RNF-08: incremento atomico del contador de cupos. El limite se verifica
     * dentro del mismo UPDATE (WHERE ... < :cupoMaximo), de modo que dos
     * turistas que confirman a la vez la misma zona y fecha no puedan pasar
     * ambos por encima del maximo. Devuelve 1 si el incremento se aplico y 0
     * si el cupo ya estaba completo.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE ControlAforo c
               SET c.cupoUtilizado = c.cupoUtilizado + 1
             WHERE c.zona.id = :idZona
               AND c.fecha = :fecha
               AND c.cupoUtilizado < :cupoMaximo
            """)
    int incrementarCupoUtilizado(@Param("idZona") Integer idZona,
                                 @Param("fecha") LocalDate fecha,
                                 @Param("cupoMaximo") Integer cupoMaximo);
}
