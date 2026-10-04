package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Carritodetalle;

import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface CarritoDetalleRepository extends JpaRepository<Carritodetalle, Long> {
    List<Carritodetalle> findByIdcarrito_IdOrderByIdAsc(long cart);

    List<Carritodetalle> findByIdcarrito_IdOrderByIdproducto_IdAsc(long cart);

    Optional<Carritodetalle> findByIdcarrito_IdAndIdproducto_Id(long cart, long product);

    Optional<Carritodetalle> findByIdAndIdcarrito_Idcomprador_IdAndIdcarrito_Estado(
            long id, long buyer, String state);

    long countByIdcarrito_Idcomprador_IdAndIdcarrito_Estado(long buyer, String state);
}