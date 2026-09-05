package com.turismo.controller;

import com.turismo.repository.AuditoriaLogRepository;
import com.turismo.repository.EstacionRepository;
import com.turismo.repository.InformePlanificacionRepository;
import com.turismo.repository.ZonaTuristicaRepository;
import com.turismo.service.ZonaTuristicaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Panel general del sistema con el resumen de zonas, estaciones e informes. */
@Controller
public class HomeController {

    private static final int ZONAS_RECIENTES = 5;

    private final ZonaTuristicaRepository zonaTuristicaRepository;
    private final EstacionRepository estacionRepository;
    private final InformePlanificacionRepository informePlanificacionRepository;
    private final AuditoriaLogRepository auditoriaLogRepository;
    private final ZonaTuristicaService zonaTuristicaService;

    public HomeController(ZonaTuristicaRepository zonaTuristicaRepository,
                           EstacionRepository estacionRepository,
                           InformePlanificacionRepository informePlanificacionRepository,
                           AuditoriaLogRepository auditoriaLogRepository,
                           ZonaTuristicaService zonaTuristicaService) {
        this.zonaTuristicaRepository = zonaTuristicaRepository;
        this.estacionRepository = estacionRepository;
        this.informePlanificacionRepository = informePlanificacionRepository;
        this.auditoriaLogRepository = auditoriaLogRepository;
        this.zonaTuristicaService = zonaTuristicaService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("totalZonas", zonaTuristicaRepository.findByEstado("Activa").size());
        model.addAttribute("totalEstaciones", estacionRepository.findByEstado("Activa").size());
        model.addAttribute("totalInformes", informePlanificacionRepository.count());
        model.addAttribute("totalAuditorias", auditoriaLogRepository.count());
        model.addAttribute("zonasRecientes", zonaTuristicaService.listarActivasConEstacionYTipos()
                .stream().limit(ZONAS_RECIENTES).toList());
        return "dashboard";
    }
}
