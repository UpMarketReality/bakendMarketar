package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Boletadetalle;

import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface BoletaDetalleRepository extends JpaRepository<Boletadetalle, Long> {
    List<Boletadetalle> findByIdboletaventa_IdOrderByIdAsc(long purchase);
}