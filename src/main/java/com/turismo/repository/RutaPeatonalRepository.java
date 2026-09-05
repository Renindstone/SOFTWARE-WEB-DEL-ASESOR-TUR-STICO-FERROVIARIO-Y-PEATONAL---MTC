package com.turismo.repository;

import com.turismo.model.RutaPeatonal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RutaPeatonalRepository extends JpaRepository<RutaPeatonal, Integer> {
    List<RutaPeatonal> findByEstacionOrigen_Id(Integer estIdEstacion);

    List<RutaPeatonal> findByZonaDestino_Id(Integer zonIdZona);

    /** Reutiliza la ruta ya calculada para un mismo par estacion-zona (RNF-04). */
    Optional<RutaPeatonal> findFirstByEstacionOrigen_IdAndZonaDestino_Id(Integer estIdEstacion, Integer zonIdZona);
}
