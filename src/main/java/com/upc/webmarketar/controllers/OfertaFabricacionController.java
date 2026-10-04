package com.upc.webmarketar.controllers;

import com.upc.webmarketar.dtos.OfertaFabricacionRequestDTO;
import com.upc.webmarketar.dtos.OfertaFabricacionResponseDTO;
import com.upc.webmarketar.service.OfertaFabricacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vendedor/solicitudes-cotizacion")
public class OfertaFabricacionController {

    private final OfertaFabricacionService ofertaFabricacionService;

    public OfertaFabricacionController(
            OfertaFabricacionService ofertaFabricacionService
    ) {
        this.ofertaFabricacionService = ofertaFabricacionService;
    }

    @PostMapping("/{idSolicitud}/ofertas")
    @PreAuthorize("hasRole('VENDEDOR')")
    public ResponseEntity<OfertaFabricacionResponseDTO> presentarOferta(
            @PathVariable Long idSolicitud,
            @RequestBody OfertaFabricacionRequestDTO datos,
            Authentication authentication)
    {
        String correoVendedor = authentication.getName();

        OfertaFabricacionResponseDTO respuesta =
                ofertaFabricacionService.presentarOferta(
                        idSolicitud,
                        correoVendedor,
                        datos
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }
}