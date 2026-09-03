package com.turismo.controller;

import com.turismo.service.AuditoriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** RF-15/RNF-07: panel de auditoria para gestores del MTC (ADMIN_MTC). */
@Controller
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/admin/auditoria")
    public String listar(Model model) {
        model.addAttribute("registros", auditoriaService.listarTodo());
        return "admin/auditoria";
    }
}
