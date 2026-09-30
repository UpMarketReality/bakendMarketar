package com.upc.webmarketar.service;

import com.upc.webmarketar.entities.Producto;
import com.upc.webmarketar.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    // ==========================================
    // LÓGICA PARA EL COMPRADOR
    // ==========================================

    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosParaComprador() {
        // Solo muestra productos "ACTIVO" y con stock > 0
        return productoRepository.findByEstadoAndStockGreaterThan("ACTIVO", 0);
    }

    @Transactional(readOnly = true)
    public Producto obtenerDetalleProducto(Long idProducto) {
        return productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    // ==========================================
    // LÓGICA PARA EL VENDEDOR
    // ==========================================

    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosPorVendedor(Long idVendedor) {
        return productoRepository.findAllByidvendedor(idVendedor);
    }

    @Transactional
    public Producto crearProducto(Producto producto) {
        producto.setEstado("ACTIVO"); // Estado por defecto según BD
        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizarProducto(Long idProducto, Producto productoActualizado) {
        return productoRepository.findById(idProducto).map(producto -> {
            producto.setNombreproducto(productoActualizado.getNombreproducto());
            producto.setDescripcion(productoActualizado.getDescripcion());
            producto.setPreciounidad(productoActualizado.getPreciounidad());
            producto.setPrecioxmayor(productoActualizado.getPrecioxmayor());
            producto.setDimensiones(productoActualizado.getDimensiones());
            producto.setMaterial(productoActualizado.getMaterial());
            producto.setColor(productoActualizado.getColor());
            producto.setStock(productoActualizado.getStock());
            producto.setEstado(productoActualizado.getEstado());
            producto.setIdcategoria(productoActualizado.getIdcategoria()); // Asumiendo mapeo 'categoria'
            return productoRepository.save(producto);
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado para actualizar"));
    }

    @Transactional
    public void eliminarProducto(Long idProducto) {
        // Borrado lógico para mantener integridad referencial
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setEstado("INACTIVO");
        productoRepository.save(producto);
    }
}
