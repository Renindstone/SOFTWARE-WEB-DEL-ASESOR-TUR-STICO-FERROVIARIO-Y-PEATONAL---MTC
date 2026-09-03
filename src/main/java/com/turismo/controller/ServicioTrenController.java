package com.turismo.controller;

import com.turismo.model.ServicioTren;
import com.turismo.service.ServicioTrenService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/** RF-12: mantenimiento de horarios y precios de los servicios de tren (admin PeruRail/MTC). */
@Controller
public class ServicioTrenController {

    private final ServicioTrenService servicioTrenService;

    public ServicioTrenController(ServicioTrenService servicioTrenService) {
        this.servicioTrenService = servicioTrenService;
    }

    @GetMapping("/admin/servicios-tren")
    public String listar(Model model) {
        model.addAttribute("servicios", servicioTrenService.listarTodos());
        model.addAttribute("servicioTren", new ServicioTren());
        return "admin/servicios-tren";
    }

    @PostMapping("/admin/servicios-tren")
    public String guardar(@Valid @ModelAttribute("servicioTren") ServicioTren servicioTren) {
        servicioTrenService.guardar(servicioTren);
        return "redirect:/admin/servicios-tren";
    }
}
