package com.turismo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UsuIdUsuario")
    private Integer usuIdUsuario;

    @Column(name = "UsuNombreUsuario", length = 50, nullable = false, unique = true)
    private String usuNombreUsuario;

    @Column(name = "UsuContrasenia", length = 100, nullable = false)
    private String usuContrasenia;

    @Column(name = "UsuNombre", length = 50, nullable = false)
    private String usuNombre;

    @Column(name = "UsuApellidos", length = 50, nullable = false)
    private String usuApellidos;

    @Column(name = "UsuEmail", length = 50, nullable = false, unique = true)
    private String usuEmail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UsuIdRol", nullable = false)
    private Rol rol;

    @Column(name = "UsuEstado", length = 10, nullable = false)
    private String usuEstado = "Activo";

    public Usuario() {
    }

    public Integer getUsuIdUsuario() {
        return usuIdUsuario;
    }

    public void setUsuIdUsuario(Integer usuIdUsuario) {
        this.usuIdUsuario = usuIdUsuario;
    }

    public String getUsuNombreUsuario() {
        return usuNombreUsuario;
    }

    public void setUsuNombreUsuario(String usuNombreUsuario) {
        this.usuNombreUsuario = usuNombreUsuario;
    }

    public String getUsuContrasenia() {
        return usuContrasenia;
    }

    public void setUsuContrasenia(String usuContrasenia) {
        this.usuContrasenia = usuContrasenia;
    }

    public String getUsuNombre() {
        return usuNombre;
    }

    public void setUsuNombre(String usuNombre) {
        this.usuNombre = usuNombre;
    }

    public String getUsuApellidos() {
        return usuApellidos;
    }

    public void setUsuApellidos(String usuApellidos) {
        this.usuApellidos = usuApellidos;
    }

    public String getUsuEmail() {
        return usuEmail;
    }

    public void setUsuEmail(String usuEmail) {
        this.usuEmail = usuEmail;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getUsuEstado() {
        return usuEstado;
    }

    public void setUsuEstado(String usuEstado) {
        this.usuEstado = usuEstado;
    }
}
