package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByIdcomprador_IdAndEstado(long buyer, String state);
}
