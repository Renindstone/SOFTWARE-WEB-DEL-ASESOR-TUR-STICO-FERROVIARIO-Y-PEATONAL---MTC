package com.turismo.controller;

import com.turismo.dto.PreferenciaDTO;
import com.turismo.dto.ZonaResultadoDTO;
import com.turismo.repository.TipoTurismoRepository;
import com.turismo.service.EstacionService;
import com.turismo.service.PreferenciaService;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * RF-01/RF-02/RF-03 (CU-01, CU-02): formulario de preferencias y seleccion
 * de estacion de partida (panel del turista).
 */
@Controller
@RequestMapping("/preferencias")
public class PreferenciaController {

    private final PreferenciaService preferenciaService;
    private final EstacionService estacionService;
    private final TipoTurismoRepository tipoTurismoRepository;

    public PreferenciaController(PreferenciaService preferenciaService,
                                  EstacionService estacionService,
                                  TipoTurismoRepository tipoTurismoRepository) {
        this.preferenciaService = preferenciaService;
        this.estacionService = estacionService;
        this.tipoTurismoRepository = tipoTurismoRepository;
    }

    @GetMapping
    public String formulario(@ModelAttribute("preferencia") PreferenciaDTO preferencia, Model model) {
        cargarCatalogos(model);
        return "cliente/preferencias";
    }

    /**
     * CN-01/CN-05: si el turista no marca ningun tipo de turismo o la
     * validacion del DTO falla, vuelve al formulario con el mensaje, sin
     * perder lo que ya habia ingresado.
     */
    @PostMapping
    public String buscarZonas(@Valid @ModelAttribute("preferencia") PreferenciaDTO preferencia,
                               BindingResult errores, Model model) {
        if (errores.hasErrors()) {
            cargarCatalogos(model);
            return "cliente/preferencias";
        }

        List<ZonaResultadoDTO> zonas = preferenciaService.buscarZonasRecomendadas(preferencia);
        model.addAttribute("zonas", zonas);
        model.addAttribute("preferencia", preferencia);
        model.addAttribute("estacionOrigen",
                estacionService.buscarActivaPorId(preferencia.getIdEstacionOrigen()));
        return "cliente/zonas-resultado";
    }

    /** RF-02: solo estaciones activas; RNF-06: tipos de turismo desde la tabla parametrica. */
    private void cargarCatalogos(Model model) {
        model.addAttribute("estaciones", estacionService.listarActivas());
        model.addAttribute("tipos", tipoTurismoRepository.findAllByOrderByNombreAsc());
    }
}
