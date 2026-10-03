package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Solicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    @Query("""
            SELECT s
            FROM Solicitud s
            JOIN s.idprototipoia p
            WHERE s.estado = 'EN_COTIZACION'
            AND (:categoryId IS NULL OR p.idcategoriaprototipo.id = :categoryId)
            """)
    Page<Solicitud> buscarSolicitudesAbiertas(
            @Param("categoryId") Long categoryId,
            Pageable pageable);

    @Query("""
        SELECT s
        FROM Solicitud s
        JOIN FETCH s.idprototipoia p
        JOIN FETCH p.idcategoriaprototipo
        WHERE s.id = :requestId
        AND s.estado = 'EN_COTIZACION'
        """)
    Optional<Solicitud> buscarSolicitudAbiertaPorId(
            @Param("requestId") Long requestId
    );
}
