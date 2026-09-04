package com.turismo.security;

import com.turismo.model.Usuario;
import com.turismo.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Carga el usuario autenticado desde PostgreSQL (tabla usuario / rol) para
 * Spring Security. Los roles ADMIN_MTC, TRAVEL_GROUP_USER y
 * TURISTA_PUBLICO se exponen como authorities con prefijo ROLE_.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByNombreUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        boolean habilitado = "Activo".equalsIgnoreCase(usuario.getEstado());

        return User.withUsername(usuario.getNombreUsuario())
                .password(usuario.getContrasenia())
                .disabled(!habilitado)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre())))
                .build();
    }
}