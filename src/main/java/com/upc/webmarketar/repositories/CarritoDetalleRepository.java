package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Carritodetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarritoDetalleRepository
        extends JpaRepository<Carritodetalle, Long> {

    List<Carritodetalle> findByIdcarrito_Id(Long idCarrito);
}