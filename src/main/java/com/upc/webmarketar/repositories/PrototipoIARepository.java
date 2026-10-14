package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Prototipoia;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface PrototipoIARepository extends JpaRepository<Prototipoia, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Prototipoia p where p.id = :id")
    Optional<Prototipoia> lockById(@Param("id") long id);

    @Query(
            "select p from Prototipoia p where p.comprador.id = :buyer and (:category is null or"
                    + " p.idcategoriaprototipo.id = :category)")
    Page<Prototipoia> search(
            @Param("buyer") long buyer, @Param("category") Long category, Pageable pageable);

    @Query(
            """
            select count(p) from Prototipoia p where p.id = :id and
            (p.comprador.usuarioid.id = :user or exists (
                select v.id from Vendedor v where v.usuarioid.id = :user and
                (exists (select s.id from Solicitud s where s.idprototipoia = p and s.estado in ('EN_COTIZACION','COTIZADA'))
                 or exists (select e.id from Encargo e where e.solicitud.idprototipoia = p and e.cotizacionprototipo.idvendedor = v))))
            """)
    long countAuthorized(@Param("id") long id, @Param("user") long user);
}
