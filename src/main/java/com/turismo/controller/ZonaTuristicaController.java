package com.turismo.controller;

import com.turismo.model.ZonaTuristica;
import com.turismo.service.EstacionService;
import com.turismo.service.ZonaTuristicaService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RF-10/RF-17 (CU-04): CRUD de zonas turisticas para Travel Group Perú, con
 * tipos de turismo y cupo diario.
 */
@Controller
@RequestMapping("/zonas")
public class ZonaTuristicaController {

    @Autowired
    private ZonaTuristicaService zonaTuristicaService;
    @Autowired
    private EstacionService estacionService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("zonas", zonaTuristicaService.listarActivas());
        model.addAttribute("zona", new ZonaTuristica());
        model.addAttribute("estaciones", estacionService.listarActivas());
        return "admin/zonas-lista";
    }
    
    @PostMapping
    public String guardar(@Valid @ModelAttribute("zona") ZonaTuristica zona,
            @RequestParam(name = "idsTipoTurismo", required = false) List<Integer> idsTipoTurismo) {
        zonaTuristicaService.registrarOActualizar(zona, idsTipoTurismo == null ? List.of() : idsTipoTurismo);
        return "redirect:/zonas";
    }
}
