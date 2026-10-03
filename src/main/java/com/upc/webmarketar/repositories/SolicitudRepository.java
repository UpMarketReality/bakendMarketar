package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Solicitud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    @Query("""
            SELECT s
            FROM Solicitud s
            JOIN s.idprototipoia p
            WHERE s.estado = 'PENDIENTE'
            AND (:categoryId IS NULL OR p.idcategoriaprototipo.id = :categoryId)
            """)
    Page<Solicitud> buscarSolicitudesAbiertas(
            @Param("categoryId") Long categoryId,
            Pageable pageable);
}
