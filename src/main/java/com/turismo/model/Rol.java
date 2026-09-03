package com.turismo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RolIdRol")
    private Integer rolIdRol;

    @Column(name = "RolNombreRol", length = 30, nullable = false, unique = true)
    private String rolNombreRol;

    public Rol() {
    }

    public Integer getRolIdRol() {
        return rolIdRol;
    }

    public void setRolIdRol(Integer rolIdRol) {
        this.rolIdRol = rolIdRol;
    }

    public String getRolNombreRol() {
        return rolNombreRol;
    }

    public void setRolNombreRol(String rolNombreRol) {
        this.rolNombreRol = rolNombreRol;
    }
}
