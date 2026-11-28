package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Imagenproducto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagenProductoRepository extends JpaRepository<Imagenproducto, Long> {
    List<Imagenproducto> findByIdproducto_IdOrderByEsprincipalDescIdAsc(long product);

    List<Imagenproducto> findByIdproducto_IdOrderByIdAsc(long product);
}
