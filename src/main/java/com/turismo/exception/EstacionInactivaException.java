package com.turismo.exception;

/** RF-02: una estacion marcada como Inactiva no puede seleccionarse como origen. */
public class EstacionInactivaException extends RuntimeException {
    public EstacionInactivaException(String message) {
        super(message);
    }
}
