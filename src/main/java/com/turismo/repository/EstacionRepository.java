package com.turismo.repository;

import com.turismo.model.Estacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EstacionRepository extends JpaRepository<Estacion, Integer> {
    Optional<Estacion> findByEstCodigo(String estCodigo);
    List<Estacion> findByEstEstado(String estEstado);
}
