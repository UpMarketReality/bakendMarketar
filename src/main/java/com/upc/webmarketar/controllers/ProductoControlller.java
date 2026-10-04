package com.upc.webmarketar.controllers;

import com.upc.webmarketar.dtos.ProductoDTO;
import com.upc.webmarketar.entities.Producto;
import com.upc.webmarketar.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
public class ProductoControlller {
    @Autowired
    private ProductoService productoService;

    // GET: Listar los productos
    @GetMapping("/producto")
    public List<Producto>  ListarProductos()
    {
        return productoService.ListarProductos();
    }
    // GET: Listar los productos de un vendedor específico
    @GetMapping("/producto/{idVendedor}")
    public ResponseEntity<List<Producto>> listarMisProductos(@PathVariable Long idVendedor) {
        List<Producto> productos = productoService.obtenerProductosPorVendedor(idVendedor);
        return ResponseEntity.ok(productos);
    }
    // GET: Listar los producto por el nombre, sirve para que los compradores busquen productos
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> buscarProductos(@RequestParam("nombre") String nombre) {
        List<ProductoDTO> productosEncontrados = productoService.buscarProductosPorNombre(nombre);
        return ResponseEntity.ok(productosEncontrados);
    }
    // GET: Listar los producto por el nombre segun idVendedor
    @GetMapping("/buscar/{idVendedor}")
    public ResponseEntity<List<ProductoDTO>> buscarProductos(@RequestParam("nombre") String nombre,  @PathVariable Long idVendedor) {
        List<ProductoDTO> productosEncontrados = productoService.buscarProductosPorVendedorNombre(idVendedor, nombre);
        return ResponseEntity.ok(productosEncontrados);
    }

    // POST: Crear un nuevo producto
    @PostMapping("/producto")
    public ResponseEntity<ProductoDTO> crearProducto(@RequestBody ProductoDTO productoDTO) {
        ProductoDTO nuevoProducto = productoService.crearProducto(productoDTO);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    // PUT: Actualizar un producto existente
    @PutMapping("/producto/{id}")
    public ResponseEntity<Producto> actualizarProducto(
            @PathVariable Long id,
            @RequestBody Producto productoActualizado) {
        Producto producto = productoService.actualizarProducto(id, productoActualizado);
        return ResponseEntity.ok(producto);
    }

    // DELETE: Eliminar un producto (Borrado lógico a INACTIVO)
    @DeleteMapping("/producto/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
