package com.turismo.repository;

import com.turismo.model.AuditoriaLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditoriaLogRepository extends JpaRepository<AuditoriaLog, Integer> {

    List<AuditoriaLog> findByTablaAfectada(String tablaAfectada);

    /** RNF-07: log del mas reciente al mas antiguo, como la vista vw_auditoria. */
    List<AuditoriaLog> findAllByOrderByFechaDescIdDesc();
}
