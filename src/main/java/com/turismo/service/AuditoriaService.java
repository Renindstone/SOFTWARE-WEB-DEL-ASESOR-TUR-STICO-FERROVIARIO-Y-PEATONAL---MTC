package com.turismo.service;

import com.turismo.model.AuditoriaLog;
import com.turismo.repository.AuditoriaLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RF-15/RNF-07: registra en AuditoriaLog cada operacion administrativa
 * (INSERT/UPDATE/DELETE) y cada sincronizacion externa (SYNC).
 * Caja Blanca: CB-07 (persistencia de valor anterior/nuevo sin nulos).
 */
@Service
public class AuditoriaService {

    private final AuditoriaLogRepository auditoriaLogRepository;

    public AuditoriaService(AuditoriaLogRepository auditoriaLogRepository) {
        this.auditoriaLogRepository = auditoriaLogRepository;
    }

    /** CB-07: registra la operacion con sus valores anterior y nuevo. */
    public AuditoriaLog registrarAuditoria(String usuario, String operacion, String tablaAfectada,
                                            String valorAnterior, String valorNuevo) {
        AuditoriaLog log = new AuditoriaLog();
        log.setUsuario(usuario);
        log.setOperacion(operacion);
        log.setTablaAfectada(tablaAfectada);
        log.setValorAnterior(valorAnterior);
        log.setValorNuevo(valorNuevo);
        return auditoriaLogRepository.save(log);
    }

    public List<AuditoriaLog> listarPorTabla(String tablaAfectada) {
        return auditoriaLogRepository.findByTablaAfectada(tablaAfectada);
    }

    public List<AuditoriaLog> listarTodo() {
        return auditoriaLogRepository.findAll();
    }
}
