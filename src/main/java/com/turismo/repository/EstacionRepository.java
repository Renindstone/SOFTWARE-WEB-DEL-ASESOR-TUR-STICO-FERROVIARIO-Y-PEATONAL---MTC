package com.turismo.repository;

import com.turismo.model.Estacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EstacionRepository extends JpaRepository<Estacion, Integer> {
    Optional<Estacion> findByCodigo(String codigo);

    List<Estacion> findByEstado(String estado);

    List<Estacion> findByEstadoOrderByNombreAsc(String estado);

    List<Estacion> findAllByOrderByNombreAsc();
}
