package com.upc.webmarketar.controller;

import com.upc.webmarketar.dto.CarritoDTO;
import com.upc.webmarketar.service.CarritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comprador/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    @PreAuthorize("hasRole('COMPRADOR')")
    public ResponseEntity<CarritoDTO> consultarCarritoActivo(Authentication authentication) {
        String correoComprador = authentication.getName();

        CarritoDTO carrito =
                carritoService.obtenerCarritoActivo(
                        correoComprador
                );

        return ResponseEntity.ok(carrito);
    }
}
