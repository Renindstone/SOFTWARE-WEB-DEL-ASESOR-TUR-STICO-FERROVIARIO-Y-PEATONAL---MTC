package com.turismo.controller;

import com.turismo.service.EstacionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** RF-11 (CU-05): consulta de estaciones en modo solo lectura para Travel Group Perú. */
@Controller
@RequestMapping("/estaciones")
public class EstacionController {

    @Autowired 
    private EstacionService estacionService;


    @GetMapping
    public String listar(Model model) {
        model.addAttribute("estaciones", estacionService.listarTodas());
        return "admin/estaciones-lista";
    }
}
