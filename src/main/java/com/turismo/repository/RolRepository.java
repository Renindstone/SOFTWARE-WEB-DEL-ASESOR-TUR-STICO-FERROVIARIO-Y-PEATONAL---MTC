package com.turismo.repository;

import com.turismo.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByRolNombreRol(String rolNombreRol);
}
