package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @EntityGraph(attributePaths = "idrol")
    Optional<Usuario> findByCorreo(String correo);
}
