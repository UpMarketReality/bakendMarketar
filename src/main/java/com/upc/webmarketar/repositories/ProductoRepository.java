package com.upc.webmarketar.repositories;

import com.upc.webmarketar.entities.Producto;
import com.upc.webmarketar.entities.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>{
    // Para el COMPRADOR: Lista todos los productos que están activos y con stock
    List<Producto> findByEstadoAndStockGreaterThan(String estado, Integer stock);

    List<Producto> findAllByVendedor_Id(Long idvendedorId);
}
