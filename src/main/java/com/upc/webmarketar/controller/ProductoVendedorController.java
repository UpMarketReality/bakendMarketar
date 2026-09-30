package com.upc.webmarketar.controller;

import com.upc.webmarketar.entities.Producto;
import com.upc.webmarketar.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductoVendedorController {
    @Autowired
    private ProductoService productoService;

    // GET: Listar los productos de un vendedor específico
    @GetMapping("/mis-productos/{idVendedor}")
    public ResponseEntity<List<Producto>> listarMisProductos(@PathVariable Long idVendedor) {
        List<Producto> productos = productoService.obtenerProductosPorVendedor(idVendedor);
        return ResponseEntity.ok(productos);
    }

    // POST: Crear un nuevo producto
    @PostMapping
    public ResponseEntity<Producto> crearProducto(@RequestBody Producto producto) {
        Producto nuevoProducto = productoService.crearProducto(producto);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    // PUT: Actualizar un producto existente
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(
            @PathVariable Long id,
            @RequestBody Producto productoActualizado) {
        Producto producto = productoService.actualizarProducto(id, productoActualizado);
        return ResponseEntity.ok(producto);
    }

    // DELETE: Eliminar un producto (Borrado lógico a INACTIVO)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
