package com.turismo.controller;

import com.turismo.model.ServicioTren;
import com.turismo.service.ServicioTrenService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** RF-12: mantenimiento de horarios y precios de los servicios de tren (admin PeruRail/MTC). */
@Controller
@RequestMapping("/servicios-tren")
public class ServicioTrenController {

    @Autowired 
    private ServicioTrenService servicioTrenService;


    @GetMapping
    public String listar(Model model) {
        model.addAttribute("servicios", servicioTrenService.listarTodos());
        model.addAttribute("servicioTren", new ServicioTren());
        return "admin/servicios-tren";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("servicioTren") ServicioTren servicioTren) {
        servicioTrenService.guardar(servicioTren);
        return "redirect:/servicios-tren";
    }
}
