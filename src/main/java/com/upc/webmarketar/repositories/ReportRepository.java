package com.upc.webmarketar.repositories;

import com.upc.webmarketar.dtos.ProductSalesDTO;
import com.upc.webmarketar.entities.Boletadetalle;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Aggregate projections stay in the persistence layer; no managed entities are returned to the API.
 */
public interface ReportRepository extends Repository<Boletadetalle, Long> {
    interface SalesTotals {
        long getPurchases();

        long getUnits();

        BigDecimal getAmount();
    }

    interface OrderTotals {
        long getQuantity();

        BigDecimal getAmount();
    }

    @Query(
            """
            select count(distinct v.id) as purchases, coalesce(sum(d.cantidad), 0) as units,
                   coalesce(sum(d.cantidad * d.preciounitario - d.descuento), 0) as amount
            from Boletadetalle d join d.idboletaventa v join d.idproducto p
            where p.vendedor.id = :seller and v.estadoemision = 'PENDIENTE'
              and v.fechaventa >= :start and v.fechaventa < :end
            """)
    SalesTotals sales(
            @Param("seller") long seller,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end);

    @Query(
            """
            select new com.upc.webmarketar.dtos.ProductSalesDTO(p.id, p.nombreproducto,
                   sum(d.cantidad), sum(d.cantidad * d.preciounitario - d.descuento))
            from Boletadetalle d join d.idboletaventa v join d.idproducto p
            where p.vendedor.id = :seller and v.estadoemision = 'PENDIENTE'
              and v.fechaventa >= :start and v.fechaventa < :end
            group by p.id, p.nombreproducto order by p.id
            """)
    List<ProductSalesDTO> products(
            @Param("seller") long seller,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end);

    @Query(
            "select count(o) from Cotizacionprototipo o where o.idvendedor.id = :seller and o.fecha"
                    + " >= :start and o.fecha < :end")
    long issued(
            @Param("seller") long seller,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end);

    @Query(
            """
            select count(e) as quantity, coalesce(sum(o.monto), 0) as amount
            from Encargo e join e.cotizacionprototipo o
            where o.idvendedor.id = :seller and e.fechacreacion >= :start and e.fechacreacion < :end
            """)
    OrderTotals orders(
            @Param("seller") long seller,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end);
}
