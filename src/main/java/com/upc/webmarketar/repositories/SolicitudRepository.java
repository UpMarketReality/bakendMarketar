package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Solicitud;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Solicitud s where s.id = :id")
    Optional<Solicitud> lockById(@Param("id") long id);

    @Query(
            """
            select s from Solicitud s where
            ((:buyer is null and s.estado in ('EN_COTIZACION','COTIZADA')) or s.idcomprador.id = :buyer)
            and (:category is null or s.idprototipoia.idcategoriaprototipo.id = :category)
            """)
    Page<Solicitud> search(
            @Param("buyer") Long buyer, @Param("category") Long category, Pageable pageable);
}
