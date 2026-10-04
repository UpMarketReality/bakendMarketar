package com.upc.webmarketar.service;

import com.upc.webmarketar.dtos.CarritoDTO;
import com.upc.webmarketar.dtos.ItemCarritoDTO;
import com.upc.webmarketar.entities.Carrito;
import com.upc.webmarketar.entities.Carritodetalle;
import com.upc.webmarketar.entities.Comprador;
import com.upc.webmarketar.repositories.CarritoDetalleRepository;
import com.upc.webmarketar.repositories.CarritoRepository;
import com.upc.webmarketar.repositories.CompradorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final CarritoDetalleRepository carritoDetalleRepository;
    private final CompradorRepository compradorRepository;

    public CarritoService(
            CarritoRepository carritoRepository,
            CarritoDetalleRepository carritoDetalleRepository,
            CompradorRepository compradorRepository
    ) {
        this.carritoRepository = carritoRepository;
        this.carritoDetalleRepository = carritoDetalleRepository;
        this.compradorRepository = compradorRepository;
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
}