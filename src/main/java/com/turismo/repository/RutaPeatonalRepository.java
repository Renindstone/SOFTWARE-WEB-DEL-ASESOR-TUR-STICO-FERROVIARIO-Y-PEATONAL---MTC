package com.turismo.repository;

import com.turismo.model.RutaPeatonal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RutaPeatonalRepository extends JpaRepository<RutaPeatonal, Integer> {
    List<RutaPeatonal> findByEstacionOrigen_Id(Integer estIdEstacion);
    List<RutaPeatonal> findByZonaDestino_Id(Integer zonIdZona);
}
