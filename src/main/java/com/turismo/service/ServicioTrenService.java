package com.turismo.service;

import com.turismo.integration.perurail.PeruRailClient;
import com.turismo.model.ServicioTren;
import com.turismo.repository.ServicioTrenRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RF-12: mantenimiento de horarios y precios de los servicios de tren
 * (administrador de PeruRail/MTC). Respalda la vista admin/servicios-tren.html.
 */
@Service
public class ServicioTrenService {

    private final ServicioTrenRepository servicioTrenRepository;
    private final PeruRailClient peruRailClient;

    public ServicioTrenService(ServicioTrenRepository servicioTrenRepository, PeruRailClient peruRailClient) {
        this.servicioTrenRepository = servicioTrenRepository;
        this.peruRailClient = peruRailClient;
    }

    public List<ServicioTren> listarPorEstacionOrigen(Integer idEstacionOrigen) {
        return servicioTrenRepository.findByEstacionOrigen_Id(idEstacionOrigen);
    }

    public List<ServicioTren> listarTodos() {
        return servicioTrenRepository.findAll();
    }

    /** CB-05/CB-06: valida la tarifa (PeruRailClient.validarTarifaPeruRail) antes de persistir. */
    public ServicioTren guardar(ServicioTren servicioTren) {
        peruRailClient.validarTarifaPeruRail(servicioTren.getTarifa());
        return servicioTrenRepository.save(servicioTren);
    }
}
