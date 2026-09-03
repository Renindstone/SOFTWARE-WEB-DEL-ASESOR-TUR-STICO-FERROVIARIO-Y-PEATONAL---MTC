package com.turismo.repository;

import com.turismo.model.AuditoriaLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditoriaLogRepository extends JpaRepository<AuditoriaLog, Integer> {
    List<AuditoriaLog> findByAudTablaAfectada(String audTablaAfectada);
}
