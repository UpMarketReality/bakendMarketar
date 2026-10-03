package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Cotizacionprototipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CotizacionPrototipoRepository
        extends JpaRepository<Cotizacionprototipo, Long> {

    boolean existsByIdsolicitud_IdAndIdvendedor_Id(
            Long idSolicitud,
            Long idVendedor
    );
}