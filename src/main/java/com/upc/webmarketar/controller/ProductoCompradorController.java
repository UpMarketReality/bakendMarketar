package com.upc.webmarketar.controller;

import com.upc.webmarketar.entities.Producto;
import com.upc.webmarketar.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductoCompradorController {
    @Autowired
    private ProductoService productoService;

    // GET: Listar todos los productos activos disponibles para comprar
    @GetMapping
    public ResponseEntity<List<Producto>> listarProductosDisponibles() {
        List<Producto> productos = productoService.obtenerProductosParaComprador();
        return ResponseEntity.ok(productos);
    }

    // GET: Ver detalle de un producto específico
    @GetMapping("/{id}")
    public ResponseEntity<Producto> verDetalleProducto(@PathVariable Long id) {
        Producto producto = productoService.obtenerDetalleProducto(id);
        return ResponseEntity.ok(producto);
    }
}
