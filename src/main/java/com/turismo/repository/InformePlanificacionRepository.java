package com.turismo.repository;

import com.turismo.model.InformePlanificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InformePlanificacionRepository extends JpaRepository<InformePlanificacion, Integer> {
    Optional<InformePlanificacion> findByCodigo(String codigo);
}
