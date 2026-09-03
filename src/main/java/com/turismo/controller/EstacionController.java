package com.turismo.controller;

import com.turismo.service.EstacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** RF-11 (CU-05): consulta de estaciones en modo solo lectura para Travel Group Perú. */
@Controller
public class EstacionController {

    private final EstacionService estacionService;

    public EstacionController(EstacionService estacionService) {
        this.estacionService = estacionService;
    }

    @GetMapping("/admin/estaciones")
    public String listar(Model model) {
        model.addAttribute("estaciones", estacionService.listarTodas());
        return "admin/estaciones-lista";
    }
}
