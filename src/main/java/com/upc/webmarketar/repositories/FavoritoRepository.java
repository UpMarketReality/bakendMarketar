package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Favorito;

import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    Optional<Favorito> findByIdcomprador_IdAndIdproducto_Id(long buyer, long product);

    List<Favorito> findByIdcomprador_IdOrderByIdDesc(long buyer);

    void deleteByIdcomprador_IdAndIdproducto_Id(long buyer, long product);
}
