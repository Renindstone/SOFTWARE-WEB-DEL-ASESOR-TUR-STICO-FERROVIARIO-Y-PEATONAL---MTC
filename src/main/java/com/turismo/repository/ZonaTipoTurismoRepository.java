package com.turismo.repository;

import com.turismo.model.ZonaTipoTurismo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ZonaTipoTurismoRepository extends JpaRepository<ZonaTipoTurismo, Integer> {
    List<ZonaTipoTurismo> findByZonaTuristica_Id(Integer zonaId);
    List<ZonaTipoTurismo> findByTipoTurismo_Id(Integer tipoTurismoId);
}
