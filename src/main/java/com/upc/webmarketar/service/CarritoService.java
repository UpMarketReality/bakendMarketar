package com.upc.webmarketar.service;

import com.upc.webmarketar.dto.CarritoDTO;
import com.upc.webmarketar.dto.ItemCarritoDTO;
import com.upc.webmarketar.entities.Carrito;
import com.upc.webmarketar.entities.Carritodetalle;
import com.upc.webmarketar.entities.Comprador;
import com.upc.webmarketar.repositories.CarritoDetalleRepository;
import com.upc.webmarketar.repositories.CarritoRepository;
import com.upc.webmarketar.repositories.CompradorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.upc.webmarketar.dto.AgregarProductoCarritoDTO;
import com.upc.webmarketar.dto.RespuestaAgregarProductoDTO;
import com.upc.webmarketar.entities.Producto;
import com.upc.webmarketar.repositories.ProductoRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final CarritoDetalleRepository carritoDetalleRepository;
    private final CompradorRepository compradorRepository;
    private final ProductoRepository productoRepository;

    public CarritoService(
            CarritoRepository carritoRepository,
            CarritoDetalleRepository carritoDetalleRepository,
            CompradorRepository compradorRepository,
            ProductoRepository productoRepository
    ) {
        this.carritoRepository = carritoRepository;
        this.carritoDetalleRepository = carritoDetalleRepository;
        this.compradorRepository = compradorRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public CarritoDTO obtenerCarritoActivo(String correoComprador) {

        Comprador comprador = compradorRepository
                .findByUsuarioid_Correo(correoComprador)
                .orElseThrow(() ->
                        new RuntimeException("Comprador no encontrado")
                );

        Carrito carrito = carritoRepository
                .findByIdcomprador_IdAndEstado(
                        comprador.getId(),
                        "ACTIVO"
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "El comprador no tiene un carrito activo"
                        )
                );

        List<Carritodetalle> detalles =
                carritoDetalleRepository.findByIdcarrito_Id(
                        carrito.getId()
                );

        List<ItemCarritoDTO> items = detalles.stream()
                .map(this::convertirAItemDTO)
                .toList();

        BigDecimal total = items.stream()
                .map(ItemCarritoDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CarritoDTO(
                carrito.getId(),
                items,
                total
        );
    }

    private ItemCarritoDTO convertirAItemDTO(Carritodetalle detalle) {
        BigDecimal subtotal =
                detalle.getPreciounitario()
                        .multiply(
                                BigDecimal.valueOf(detalle.getCantidad())
                        );

        return new ItemCarritoDTO(
                detalle.getId(),
                detalle.getIdproducto().getId(),
                detalle.getIdproducto().getNombreproducto(),
                detalle.getCantidad(),
                detalle.getPreciounitario(),
                subtotal
        );
    }

    @Transactional
    public RespuestaAgregarProductoDTO agregarProducto(String correoComprador, AgregarProductoCarritoDTO datos) {

        // Validacion de cantidad
        if (datos.getCantidad() == null || datos.getCantidad() <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor que cero");
        }

        Comprador comprador = compradorRepository
                .findByUsuarioid_Correo(correoComprador)
                .orElseThrow(() -> new RuntimeException("Comprador no encontrado"));

        Carrito carrito = carritoRepository
                .findByIdcomprador_IdAndEstado(comprador.getId(),"ACTIVO")
                .orElseThrow(() -> new RuntimeException("El comprador no tiene un carrito activo"));

        Producto producto = productoRepository
                .findById(datos.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Carritodetalle detalleExistente = carritoDetalleRepository
                        .findByIdcarrito_IdAndIdproducto_Id(
                                carrito.getId(),
                                producto.getId())
                        .orElse(null);

        Carritodetalle detalle;
        boolean nuevo;

        // Si existe, acumular cantidad
        if (detalleExistente != null) {
            detalleExistente.setCantidad(
                    detalleExistente.getCantidad()
                            + datos.getCantidad());

            detalle = carritoDetalleRepository.save(
                    detalleExistente);

            nuevo = false;

        }
        // Si no existe, crear nuevo item
        else {
            detalle = new Carritodetalle();

            detalle.setIdcarrito(carrito);
            detalle.setIdproducto(producto);
            detalle.setCantidad(datos.getCantidad());
            detalle.setPreciounitario(producto.getPreciounidad());

            detalle = carritoDetalleRepository.save(detalle);

            nuevo = true;
        }

        ItemCarritoDTO item = convertirAItemDTO(detalle);

        List<Carritodetalle> detalles =
                carritoDetalleRepository
                        .findByIdcarrito_Id(carrito.getId());

        BigDecimal total = detalles.stream()
                .map(d -> d.getPreciounitario().multiply(
                                BigDecimal.valueOf(d.getCantidad())))
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        return new RespuestaAgregarProductoDTO(
                item,
                total,
                nuevo
        );
    }
}