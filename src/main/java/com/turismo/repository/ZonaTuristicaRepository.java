package com.turismo.repository;

import com.turismo.model.ZonaTuristica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ZonaTuristicaRepository extends JpaRepository<ZonaTuristica, Integer> {
    List<ZonaTuristica> findByEstacionCercana_Id(Integer estIdEstacion);
    List<ZonaTuristica> findByEstado(String estado);
}
