package com.upc.webmarketar.controller;

import com.upc.webmarketar.dto.SolicitudResumenDTO;
import com.upc.webmarketar.service.SolicitudService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/seller/quote-requests")
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

}
