package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Comprador;
import com.upc.webmarketar.entities.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompradorRepository extends JpaRepository<Comprador, Long> {
}
