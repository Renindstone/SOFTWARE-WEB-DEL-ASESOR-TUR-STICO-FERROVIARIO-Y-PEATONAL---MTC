package com.turismo.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Manejador global de excepciones de negocio. Traduce cada excepcion del
 * dominio a la vista de error correspondiente, evitando que el usuario
 * final reciba un stacktrace crudo (RNF-02, usabilidad).
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            EstacionInactivaException.class,
            AforoCompletoException.class,
            RutaInvalidaException.class,
            FeedInvalidoException.class,
            TarifaInvalidaException.class
    })
    public String manejarExcepcionesDeNegocio(RuntimeException ex, Model model) {
        model.addAttribute("mensajeError", ex.getMessage());
        return "error";
    }
}
