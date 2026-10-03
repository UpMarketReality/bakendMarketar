package com.upc.webmarketar.controller;

import com.upc.webmarketar.dto.SolicitudDetalleDTO;
import com.upc.webmarketar.dto.SolicitudResumenDTO;
import com.upc.webmarketar.service.SolicitudService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vendedor/solicitudes-cotizacion")
public class SolicitudVendedorController {
    private final SolicitudService solicitudService;

    public SolicitudVendedorController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @GetMapping
    @PreAuthorize("hasRole('VENDEDOR')")
    public ResponseEntity<Page<SolicitudResumenDTO>> listarSolicitudesAbiertas(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Page<SolicitudResumenDTO> solicitudes =
                solicitudService.listarSolicitudesAbiertas(
                        categoryId,
                        page,
                        size
                );

        return ResponseEntity.ok(solicitudes);
    }

    @GetMapping("/{idSolicitud}")
    @PreAuthorize("hasRole('VENDEDOR')")
    public ResponseEntity<SolicitudDetalleDTO> obtenerSolicitudAbiertaPorId(
            @PathVariable Long idSolicitud
    ) {

        SolicitudDetalleDTO solicitud =
                solicitudService.obtenerSolicitudAbiertaPorId(idSolicitud);

        return ResponseEntity.ok(solicitud);
    }

}
