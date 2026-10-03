package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Comprador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompradorRepository extends JpaRepository<Comprador, Long> {
    Optional<Comprador> findByUsuarioid_Id(Long idUsuario);

    Optional<Comprador> findByUsuarioid_Correo(String correo);
}
