package com.turismo.controller;

import com.turismo.model.ZonaTuristica;
import com.turismo.service.EstacionService;
import com.turismo.service.ZonaTuristicaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** RF-10/RF-17 (CU-04): CRUD de zonas turisticas para Travel Group Perú, con tipos de turismo y cupo diario. */
@Controller
@RequestMapping("/admin/zonas")
public class ZonaTuristicaController {

    private final ZonaTuristicaService zonaTuristicaService;
    private final EstacionService estacionService;

    public ZonaTuristicaController(ZonaTuristicaService zonaTuristicaService, EstacionService estacionService) {
        this.zonaTuristicaService = zonaTuristicaService;
        this.estacionService = estacionService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("zonas", zonaTuristicaService.listarActivas());
        return "admin/zonas-lista";
    }

    @GetMapping("/nueva")
    public String formularioNueva(Model model) {
        model.addAttribute("zona", new ZonaTuristica());
        model.addAttribute("estaciones", estacionService.listarActivas());
        return "admin/zona-form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("zona") ZonaTuristica zona,
                           @RequestParam(name = "idsTipoTurismo", required = false) List<Integer> idsTipoTurismo) {
        zonaTuristicaService.registrarOActualizar(zona, idsTipoTurismo == null ? List.of() : idsTipoTurismo);
        return "redirect:/admin/zonas";
    }
}
