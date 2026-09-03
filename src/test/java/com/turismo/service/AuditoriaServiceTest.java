package com.turismo.service;

import com.turismo.model.AuditoriaLog;
import com.turismo.repository.AuditoriaLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Caja Blanca: CB-07 (registrarAuditoria - persiste valor anterior y
 * nuevo sin nulos para una operacion UPDATE).
 */
@ExtendWith(MockitoExtension.class)
class AuditoriaServiceTest {

    @Mock
    private AuditoriaLogRepository auditoriaLogRepository;

    @InjectMocks
    private AuditoriaService auditoriaService;

    @Test
    void cb07_registraLaOperacionConValorAnteriorYNuevo() {
        when(auditoriaLogRepository.save(any(AuditoriaLog.class))).thenAnswer(inv -> inv.getArgument(0));

        AuditoriaLog resultado = auditoriaService.registrarAuditoria(
                "admin_mtc", "UPDATE", "estacion", "EstEstado=Activa", "EstEstado=Inactiva");

        ArgumentCaptor<AuditoriaLog> captor = ArgumentCaptor.forClass(AuditoriaLog.class);
        verify(auditoriaLogRepository).save(captor.capture());

        assertThat(captor.getValue().getAudOperacion()).isEqualTo("UPDATE");
        assertThat(captor.getValue().getAudValorAnterior()).isNotNull();
        assertThat(captor.getValue().getAudValorNuevo()).isNotNull();
        assertThat(resultado.getAudTablaAfectada()).isEqualTo("estacion");
    }
}
