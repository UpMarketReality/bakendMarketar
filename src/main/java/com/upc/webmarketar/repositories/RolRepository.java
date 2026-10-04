package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Rol;

import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
}

