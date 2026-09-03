package com.turismo.exception;

/**
 * RNF-04 - Caso de prueba CB-02: se lanza cuando la estacion de origen y la
 * zona turistica de destino resultan en una distancia de ida igual a cero
 * (no existe circuito caminable valido).
 */
public class RutaInvalidaException extends RuntimeException {
    public RutaInvalidaException(String message) {
        super(message);
    }
}
