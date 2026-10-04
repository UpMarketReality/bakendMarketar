package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Comprador;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface CompradorRepository extends JpaRepository<Comprador, Long> {
    Optional<Comprador> findByUsuarioid_Id(Long user);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Comprador c where c.id = :id")
    Optional<Comprador> lockById(@Param("id") long id);
}
