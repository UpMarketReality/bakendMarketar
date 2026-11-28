package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Imagenprototipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagenPrototipoRepository extends JpaRepository<Imagenprototipo, Long> {
    List<Imagenprototipo> findByIdprototipoia_IdOrderByEsprincipalDescIdAsc(long prototype);
}
