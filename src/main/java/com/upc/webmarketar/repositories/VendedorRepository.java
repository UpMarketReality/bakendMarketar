package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Vendedor;

import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface VendedorRepository extends JpaRepository<Vendedor, Long> {
    Optional<Vendedor> findByUsuarioid_Id(Long user);
}
