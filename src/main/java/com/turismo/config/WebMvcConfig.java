package com.turismo.config;

import com.turismo.model.Estacion;
import com.turismo.repository.EstacionRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Conversores de la capa de presentacion. Los formularios de zonas
 * turisticas y de servicios de tren envian el identificador de la estacion
 * en un &lt;select&gt;; sin este conversor, Spring MVC no sabria transformar
 * ese "4" en la entidad Estacion que espera el th:field del formulario.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final EstacionRepository estacionRepository;

    public WebMvcConfig(EstacionRepository estacionRepository) {
        this.estacionRepository = estacionRepository;
    }

    @Override
    public void addFormatters(@NonNull FormatterRegistry registry) {
        registry.addConverter(new EstacionPorIdConverter(estacionRepository));
        registry.addConverter(new EstacionAIdConverter());
    }

    /** Convierte el valor de un &lt;option&gt; (EstIdEstacion) en la entidad Estacion. */
    static class EstacionPorIdConverter implements Converter<String, Estacion> {

        private final EstacionRepository estacionRepository;

        EstacionPorIdConverter(EstacionRepository estacionRepository) {
            this.estacionRepository = estacionRepository;
        }

        @Override
        public Estacion convert(@NonNull String origen) {
            if (origen.isBlank()) {
                return null;
            }
            Integer id = Integer.valueOf(origen);
            return estacionRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Estación no encontrada: " + id));
        }
    }

    /**
     * Conversion inversa: al pintar un formulario de edicion, el &lt;select&gt;
     * de Spring compara el valor de cada &lt;option&gt; con la entidad ya
     * asociada. Sin este conversor usaria el toString() de la entidad y nunca
     * marcaria la opcion correcta.
     */
    static class EstacionAIdConverter implements Converter<Estacion, String> {

        @Override
        public String convert(@NonNull Estacion estacion) {
            return estacion.getId() == null ? "" : String.valueOf(estacion.getId());
        }
    }
}
