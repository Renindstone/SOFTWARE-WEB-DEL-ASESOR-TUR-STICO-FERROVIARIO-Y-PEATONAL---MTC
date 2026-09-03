package com.turismo.controller;

import com.turismo.dto.RutaCalculadaDTO;
import com.turismo.repository.EstacionRepository;
import com.turismo.repository.ZonaTuristicaRepository;
import com.turismo.service.RutaPeatonalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** RF-04/RF-05: detalle de la ruta peatonal de ida y vuelta calculada por RutaPeatonalService. */
@Controller
public class RutaController {

    private final RutaPeatonalService rutaPeatonalService;
    private final EstacionRepository estacionRepository;
    private final ZonaTuristicaRepository zonaTuristicaRepository;

    public RutaController(RutaPeatonalService rutaPeatonalService,
                           EstacionRepository estacionRepository,
                           ZonaTuristicaRepository zonaTuristicaRepository) {
        this.rutaPeatonalService = rutaPeatonalService;
        this.estacionRepository = estacionRepository;
        this.zonaTuristicaRepository = zonaTuristicaRepository;
    }

    @GetMapping("/rutas/detalle")
    public String detalle(@RequestParam Integer idEstacion, @RequestParam Integer idZona, Model model) {
        var origen = estacionRepository.findById(idEstacion)
                .orElseThrow(() -> new IllegalArgumentException("Estación no encontrada: " + idEstacion));
        var destino = zonaTuristicaRepository.findById(idZona)
                .orElseThrow(() -> new IllegalArgumentException("Zona turística no encontrada: " + idZona));

        RutaCalculadaDTO ruta = rutaPeatonalService.calcularRutaPeatonalIdaVuelta(origen, destino);
        model.addAttribute("ruta", ruta);
        model.addAttribute("origen", origen);
        model.addAttribute("destino", destino);
        return "cliente/ruta-detalle";
    }
}
