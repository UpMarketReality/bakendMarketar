package com.upc.webmarketar.service;

import com.upc.webmarketar.dto.ProductoDTO;
import com.upc.webmarketar.entities.Categoriaproducto;
import com.upc.webmarketar.entities.Producto;
import com.upc.webmarketar.entities.Vendedor;
import com.upc.webmarketar.repositories.CategoriaProductoRepository;
import com.upc.webmarketar.repositories.ProductoRepository;
import com.upc.webmarketar.repositories.VendedorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private VendedorRepository vendedorRepository;
    @Autowired
    private CategoriaProductoRepository categoriaProductoRepository;

    @Autowired
    private ModelMapper modelMapper; // Inyectamos ModelMapper

    public List<Producto> ListarProductos()
    {
        return productoRepository.findAll();
    }

    // ==========================================
    // LÓGICA PARA EL VENDEDOR
    // ==========================================

    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosPorVendedor(Long idVendedor) {
        return productoRepository.findAllByVendedor_Id(idVendedor);
    }

    @Transactional
    public ProductoDTO crearProducto(ProductoDTO productoDTO) {
        // 1. Buscar las entidades relacionadas
        Vendedor vendedor = vendedorRepository.findById(productoDTO.getIdVendedor())
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));

        Categoriaproducto categoria = categoriaProductoRepository.findById(productoDTO.getIdcategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        // 2. Mapear mágicamente los campos básicos (nombre, precio, stock, etc.)
        Producto producto = modelMapper.map(productoDTO, Producto.class);

        // 3. Asignar las relaciones y campos por defecto manualmente
        producto.setVendedor(vendedor);
        producto.setCategoria(categoria);
        producto.setEstado("ACTIVO");

        // 4. Guardar en base de datos
        Producto productoGuardado = productoRepository.save(producto);

        // 5. Mapear de vuelta a DTO
        ProductoDTO responseDTO = modelMapper.map(productoGuardado, ProductoDTO.class);

        // (Opcional) Si ModelMapper no detecta automáticamente los IDs por diferencias de nombres:
        responseDTO.setIdVendedor(vendedor.getId());
        responseDTO.setIdcategoria(categoria.getId());

        return responseDTO;
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
            producto.setCategoria(productoActualizado.getCategoria()); // Asumiendo mapeo 'categoria'
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
