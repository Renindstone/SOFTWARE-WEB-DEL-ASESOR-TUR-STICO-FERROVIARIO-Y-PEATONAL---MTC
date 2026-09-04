package com.turismo.repository;

import com.turismo.model.ServicioTren;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServicioTrenRepository extends JpaRepository<ServicioTren, Integer> {
    List<ServicioTren> findByEstacionOrigen_Id(Integer estIdEstacion);
}
