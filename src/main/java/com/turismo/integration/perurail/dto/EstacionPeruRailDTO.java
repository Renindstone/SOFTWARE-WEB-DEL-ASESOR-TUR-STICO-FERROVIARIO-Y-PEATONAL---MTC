package com.turismo.integration.perurail.dto;

import java.math.BigDecimal;

/** Representa el registro de estacion tal como lo entrega el feed de PeruRail. */
public class EstacionPeruRailDTO {
    private String codigo;
    private String nombre;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private BigDecimal altitud;
    private String ciudad;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getLatitud() { return latitud; }
    public void setLatitud(BigDecimal latitud) { this.latitud = latitud; }
    public BigDecimal getLongitud() { return longitud; }
    public void setLongitud(BigDecimal longitud) { this.longitud = longitud; }
    public BigDecimal getAltitud() { return altitud; }
    public void setAltitud(BigDecimal altitud) { this.altitud = altitud; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
}
