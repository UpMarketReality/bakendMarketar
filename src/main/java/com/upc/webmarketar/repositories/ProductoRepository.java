package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Producto;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.id = :id")
    Optional<Producto> lockById(@Param("id") long id);

    @Query(
            """
            select p from Producto p where
            ((:seller is null and p.estado in ('ACTIVO','AGOTADO')) or p.Vendedor.id = :seller)
            and (:search is null or lower(p.nombreproducto) like :search escape '!')
            and (:category is null or p.Categoria.id = :category)
            and (:min is null or p.preciounidad >= :min)
            and (:max is null or p.preciounidad <= :max)
            """)
    Page<Producto> search(
            @Param("seller") Long seller,
            @Param("search") String search,
            @Param("category") Long category,
            @Param("min") BigDecimal min,
            @Param("max") BigDecimal max,
            Pageable pageable);
}
