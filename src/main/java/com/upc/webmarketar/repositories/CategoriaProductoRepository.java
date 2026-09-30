package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Categoriaproducto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaProductoRepository extends JpaRepository<Categoriaproducto, Long> {
}
