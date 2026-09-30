package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Prototipoia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrototipoIARepository extends JpaRepository<Prototipoia, Long> {
    List<Prototipoia> findAllByComprador_Id(Long idComprador);
}
