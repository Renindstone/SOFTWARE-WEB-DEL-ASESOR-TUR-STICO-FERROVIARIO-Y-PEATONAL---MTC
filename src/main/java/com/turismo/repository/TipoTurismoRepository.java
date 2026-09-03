package com.turismo.repository;

import com.turismo.model.TipoTurismo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoTurismoRepository extends JpaRepository<TipoTurismo, Integer> {
}
