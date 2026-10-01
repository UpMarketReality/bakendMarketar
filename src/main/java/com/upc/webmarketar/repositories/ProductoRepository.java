package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>{
    // Para el COMPRADOR: Lista todos los productos que están activos y con stock
    List<Producto> findByEstadoAndStockGreaterThan(String estado, Integer stock);

    List<Producto> findAllByVendedor_Id(Long idvendedorId);

    List<Producto> findByVendedor_IdAndNombreproductoContainingIgnoreCase(Long vendedorId, String nombreproducto);

    // Buscar por nombre PERO solo salgan los "ACTIVOS"
    List<Producto> findByNombreproductoContainingIgnoreCaseAndEstado(String nombreproducto, String estado);

}
