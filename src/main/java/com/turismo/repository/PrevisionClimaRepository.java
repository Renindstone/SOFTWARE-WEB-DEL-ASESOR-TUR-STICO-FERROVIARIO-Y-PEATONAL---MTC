package com.turismo.repository;

import com.turismo.model.PrevisionClima;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface PrevisionClimaRepository extends JpaRepository<PrevisionClima, Integer> {
    Optional<PrevisionClima> findByEstacion_IdAndFecha(Integer idEstacion, LocalDate fecha);
}
