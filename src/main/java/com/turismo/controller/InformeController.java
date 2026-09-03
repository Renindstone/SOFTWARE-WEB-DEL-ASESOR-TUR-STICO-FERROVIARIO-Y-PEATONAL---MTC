package com.turismo.controller;

import com.turismo.dto.InformeConsolidadoDTO;
import com.turismo.repository.EstacionRepository;
import com.turismo.repository.ServicioTrenRepository;
import com.turismo.repository.ZonaTuristicaRepository;
import com.turismo.service.InformeService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;

/** RF-08 (CU-01/CU-08): genera el informe consolidado de la visita, en vista web y exportacion PDF. */
@Controller
public class InformeController {

    private final InformeService informeService;
    private final EstacionRepository estacionRepository;
    private final ZonaTuristicaRepository zonaTuristicaRepository;
    private final ServicioTrenRepository servicioTrenRepository;

    public InformeController(InformeService informeService,
                              EstacionRepository estacionRepository,
                              ZonaTuristicaRepository zonaTuristicaRepository,
                              ServicioTrenRepository servicioTrenRepository) {
        this.informeService = informeService;
        this.estacionRepository = estacionRepository;
        this.zonaTuristicaRepository = zonaTuristicaRepository;
        this.servicioTrenRepository = servicioTrenRepository;
    }

    @GetMapping("/informes/consolidado")
    public String generar(@RequestParam Integer idEstacion, @RequestParam Integer idZona,
                           @RequestParam(required = false) Integer idServicioTren,
                           @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate fechaVisita,
                           Model model) {
        var origen = estacionRepository.findById(idEstacion)
                .orElseThrow(() -> new IllegalArgumentException("Estación no encontrada: " + idEstacion));
        var destino = zonaTuristicaRepository.findById(idZona)
                .orElseThrow(() -> new IllegalArgumentException("Zona turística no encontrada: " + idZona));
        var servicio = idServicioTren == null ? null : servicioTrenRepository.findById(idServicioTren).orElse(null);

        InformeConsolidadoDTO informe = informeService.generarInformeConsolidado(origen, destino, servicio, fechaVisita);
        model.addAttribute("informe", informe);
        return "informes/informe-consolidado";
    }

    @GetMapping(value = "/informes/consolidado/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @ResponseBody
    public byte[] exportarPdf(@RequestParam Integer idEstacion, @RequestParam Integer idZona,
                               @RequestParam(required = false) Integer idServicioTren,
                               @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate fechaVisita) {
        var origen = estacionRepository.findById(idEstacion)
                .orElseThrow(() -> new IllegalArgumentException("Estación no encontrada: " + idEstacion));
        var destino = zonaTuristicaRepository.findById(idZona)
                .orElseThrow(() -> new IllegalArgumentException("Zona turística no encontrada: " + idZona));
        var servicio = idServicioTren == null ? null : servicioTrenRepository.findById(idServicioTren).orElse(null);

        InformeConsolidadoDTO informe = informeService.generarInformeConsolidado(origen, destino, servicio, fechaVisita);
        return informeService.exportarPdf(informe);
    }
}
