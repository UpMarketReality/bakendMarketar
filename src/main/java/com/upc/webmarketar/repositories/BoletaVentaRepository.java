package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Boletaventa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface BoletaVentaRepository extends JpaRepository<Boletaventa, Long> {
    Optional<Boletaventa> findByIdcomprador_IdAndClaveidempotencia(long buyer, String key);

    Optional<Boletaventa> findByIdAndIdcomprador_Id(long id, long buyer);

    Page<Boletaventa> findByIdcomprador_Id(long buyer, Pageable pageable);
}
