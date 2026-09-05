package com.turismo.controller;

import com.turismo.model.ZonaTuristica;
import com.turismo.repository.TipoTurismoRepository;
import com.turismo.service.AuditoriaService;
import com.turismo.service.EstacionService;
import com.turismo.service.ZonaTuristicaService;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * RF-10/RF-17 (CU-04): CRUD de zonas turisticas para Travel Group Perú, con
 * tipos de turismo y cupo diario.
 */
@Controller
@RequestMapping("/zonas")
public class ZonaTuristicaController {

    private final ZonaTuristicaService zonaTuristicaService;
    private final EstacionService estacionService;
    private final TipoTurismoRepository tipoTurismoRepository;
    private final AuditoriaService auditoriaService;

    public ZonaTuristicaController(ZonaTuristicaService zonaTuristicaService,
                                    EstacionService estacionService,
                                    TipoTurismoRepository tipoTurismoRepository,
                                    AuditoriaService auditoriaService) {
        this.zonaTuristicaService = zonaTuristicaService;
        this.estacionService = estacionService;
        this.tipoTurismoRepository = tipoTurismoRepository;
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("zonas", zonaTuristicaService.listarTodasConEstacionYTipos());
        if (!model.containsAttribute("zona")) {
            model.addAttribute("zona", new ZonaTuristica());
            model.addAttribute("idsTipoTurismo", List.of());
        }
        cargarCatalogos(model);
        return "admin/zonas-lista";
    }

    /** CU-04: formulario de alta en pagina propia (admin/zona-form.html). */
    @GetMapping("/nueva")
    public String formularioNueva(Model model) {
        model.addAttribute("zona", new ZonaTuristica());
        model.addAttribute("idsTipoTurismo", List.of());
        cargarCatalogos(model);
        return "admin/zona-form";
    }

    /** CU-04: edicion de una zona existente, con sus tipos ya preseleccionados. */
    @GetMapping("/{id}/editar")
    public String formularioEditar(@PathVariable Integer id, Model model) {
        ZonaTuristica zona = zonaTuristicaService.buscarParaEdicion(id)
                .orElseThrow(() -> new IllegalArgumentException("Zona turística no encontrada: " + id));
        model.addAttribute("zona", zona);
        model.addAttribute("idsTipoTurismo", zonaTuristicaService.listarIdsTipoTurismo(id));
        cargarCatalogos(model);
        return "admin/zona-form";
    }

    /**
     * CN-04/CN-05: guarda la zona con sus tipos de turismo. Si falta el tipo
     * de turismo o la validacion de campos falla, vuelve al formulario con el
     * mensaje en vez de perder lo ya escrito.
     */
    @PostMapping
    public String guardar(@Valid @ModelAttribute("zona") ZonaTuristica zona,
                           BindingResult errores,
                           @RequestParam(name = "idsTipoTurismo", required = false) List<Integer> idsTipoTurismo,
                           Model model) {
        List<Integer> tipos = idsTipoTurismo == null ? List.of() : idsTipoTurismo;

        if (tipos.isEmpty()) {
            errores.rejectValue("tiposTurismo", "tipos.requeridos",
                    "Debe seleccionar al menos un tipo de turismo");
        }
        if (errores.hasErrors()) {
            model.addAttribute("idsTipoTurismo", tipos);
            cargarCatalogos(model);
            return "admin/zona-form";
        }

        zonaTuristicaService.registrarOActualizar(zona, tipos, auditoriaService.usuarioActual());
        return "redirect:/zonas";
    }

    /** RF-10: baja de la zona turistica (ZonEstado = Inactiva), auditada. */
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id) {
        zonaTuristicaService.eliminar(id, auditoriaService.usuarioActual());
        return "redirect:/zonas";
    }

    private void cargarCatalogos(Model model) {
        model.addAttribute("estaciones", estacionService.listarActivas());
        model.addAttribute("tipos", tipoTurismoRepository.findAllByOrderByNombreAsc());
    }
}
