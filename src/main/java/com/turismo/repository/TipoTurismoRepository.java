package com.turismo.repository;

import com.turismo.model.TipoTurismo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/** RNF-06: tabla parametrica; los tipos se leen de la BD, nunca del codigo. */
public interface TipoTurismoRepository extends JpaRepository<TipoTurismo, Integer> {

    List<TipoTurismo> findAllByOrderByNombreAsc();

    Optional<TipoTurismo> findByNombre(String nombre);
}
