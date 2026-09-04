package com.turismo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UsuIdUsuario")
    private Integer id;

    @Column(name = "UsuNombreUsuario", length = 50, nullable = false, unique = true)
    private String nombreUsuario;

    @Column(name = "UsuContrasenia", length = 100, nullable = false)
    private String contrasenia;

    @Column(name = "UsuNombre", length = 50, nullable = false)
    private String nombre;

    @Column(name = "UsuApellidos", length = 50, nullable = false)
    private String apellidos;

    @Column(name = "UsuEmail", length = 50, nullable = false, unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UsuIdRol", nullable = false)
    private Rol rol;

    @Column(name = "UsuEstado", length = 10, nullable = false)
    private String estado = "Activo";

}
