package com.turismo.config;

import com.turismo.security.AccesoDenegadoHandler;
import com.turismo.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Seguridad por roles (HURF06): ADMIN_MTC, TRAVEL_GROUP_USER, TURISTA_PUBLICO.
 * Modulo de administracion restringido; panel del turista y consulta de
 * zonas/estaciones abiertos a TURISTA_PUBLICO; CRUD de zonas y consulta de
 * estaciones restringidos a TRAVEL_GROUP_USER; auditoria y mantenimiento
 * de horarios/precios restringidos a ADMIN_MTC.
 */
@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final AccesoDenegadoHandler accesoDenegadoHandler;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                           AccesoDenegadoHandler accesoDenegadoHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.accesoDenegadoHandler = accesoDenegadoHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(customUserDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/css/**", "/js/**", "/img/**", "/preferencias/**").permitAll()
                        .requestMatchers("/admin/auditoria/**").hasRole("ADMIN_MTC")
                        .requestMatchers("/admin/servicios-tren/**").hasRole("ADMIN_MTC")
                        .requestMatchers("/admin/zonas/**").hasAnyRole("ADMIN_MTC", "TRAVEL_GROUP_USER")
                        .requestMatchers("/admin/estaciones/**").hasAnyRole("ADMIN_MTC", "TRAVEL_GROUP_USER")
                        .requestMatchers("/admin/**").hasRole("ADMIN_MTC")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(AbstractHttpConfigurer::disable)
                .exceptionHandling(handling -> handling.accessDeniedHandler(accesoDenegadoHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        return http.build();
    }
}
