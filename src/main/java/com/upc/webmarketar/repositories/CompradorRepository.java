package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Comprador;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompradorRepository extends JpaRepository<Comprador, Long> {
    Optional<Comprador> findByUsuarioid_Id(Long user);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Comprador c where c.id = :id")
    Optional<Comprador> lockById(@Param("id") long id);
}
