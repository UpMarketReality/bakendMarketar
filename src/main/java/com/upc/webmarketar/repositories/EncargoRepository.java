package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Encargo;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface EncargoRepository extends JpaRepository<Encargo, Long> {
    Optional<Encargo> findBySolicitud_Id(long request);

    @Query("select e.solicitud.id from Encargo e where e.id = :id")
    Optional<Long> requestId(@Param("id") long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Encargo e where e.id = :id")
    Optional<Encargo> lockById(@Param("id") long id);

    @Query(
            "select e from Encargo e where (:seller = true and e.cotizacionprototipo.idvendedor.id"
                    + " = :who) or (:seller = false and e.solicitud.idcomprador.id = :who)")
    Page<Encargo> search(
            @Param("seller") boolean seller, @Param("who") long who, Pageable pageable);
}

