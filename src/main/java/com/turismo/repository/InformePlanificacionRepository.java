package com.turismo.repository;

import com.turismo.model.InformePlanificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InformePlanificacionRepository extends JpaRepository<InformePlanificacion, Integer> {

    Optional<InformePlanificacion> findByCodigo(String codigo);

    /** Ultimo informe emitido; base del correlativo INF-0001 (diccionario 6.4). */
    Optional<InformePlanificacion> findTopByOrderByIdDesc();
}
