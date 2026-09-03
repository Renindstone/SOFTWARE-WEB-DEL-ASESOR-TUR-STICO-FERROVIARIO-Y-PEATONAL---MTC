package com.turismo.controller;

import com.turismo.dto.PreferenciaDTO;
import com.turismo.dto.ZonaResultadoDTO;
import com.turismo.service.EstacionService;
import com.turismo.service.PreferenciaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/** RF-01/RF-02/RF-03: formulario de preferencias y seleccion de estacion (panel del turista). */
@Controller
public class PreferenciaController {

    private final PreferenciaService preferenciaService;
    private final EstacionService estacionService;

    public PreferenciaController(PreferenciaService preferenciaService, EstacionService estacionService) {
        this.preferenciaService = preferenciaService;
        this.estacionService = estacionService;
    }

    @GetMapping("/preferencias")
    public String formulario(Model model) {
        model.addAttribute("estaciones", estacionService.listarActivas());
        model.addAttribute("preferencia", new PreferenciaDTO());
        return "cliente/preferencias";
    }

    @PostMapping("/preferencias")
    public String buscarZonas(@Valid @ModelAttribute("preferencia") PreferenciaDTO preferencia, Model model) {
        List<ZonaResultadoDTO> zonas = preferenciaService.buscarZonasRecomendadas(preferencia);
        model.addAttribute("zonas", zonas);
        return "cliente/zonas-resultado";
    }
}
