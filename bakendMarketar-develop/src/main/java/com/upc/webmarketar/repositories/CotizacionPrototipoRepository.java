package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Cotizacionprototipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CotizacionPrototipoRepository extends JpaRepository<Cotizacionprototipo, Long> {
    List<Cotizacionprototipo> findByIdsolicitud_IdOrderByMontoAscIdAsc(long request);

    long countByIdsolicitud_Id(long request);

    boolean existsByIdsolicitud_IdAndIdvendedor_Id(long request, long seller);

    List<Cotizacionprototipo> findByIdsolicitud_IdAndEstado(long request, String state);
}
