package com.turismo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * HURF06: pantallas de acceso. El cierre de sesion lo atiende el
 * LogoutFilter de Spring Security (POST /logout, configurado en
 * SecurityConfig), que invalida la sesion y borra la cookie; por eso aqui
 * no hay un mapeo propio de /logout.
 */
@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "auth/acceso-denegado";
    }
}
