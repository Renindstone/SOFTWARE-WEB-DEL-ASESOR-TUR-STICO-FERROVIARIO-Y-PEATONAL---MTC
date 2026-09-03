package com.turismo.repository;

import com.turismo.model.ControlAforo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface ControlAforoRepository extends JpaRepository<ControlAforo, Integer> {
    Optional<ControlAforo> findByZona_ZonIdZonaAndAfoFecha(Integer zonIdZona, LocalDate afoFecha);
}
