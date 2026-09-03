package com.turismo.repository;

import com.turismo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsuNombreUsuario(String usuNombreUsuario);
    Optional<Usuario> findByUsuEmail(String usuEmail);
}
