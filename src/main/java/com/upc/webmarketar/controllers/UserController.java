package com.upc.webmarketar.controllers;

import com.upc.webmarketar.dtos.*;
import com.upc.webmarketar.serviceimpl.UsuarioPerfilService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuario")
public class UserController {
    @Autowired
    private UsuarioPerfilService service;

    @GetMapping("/me")
    public PerfilUsuarioResponse obtenerMiPerfil(
            Authentication auth) {

        return service.obtenerMiPerfil(auth);
    }

    @GetMapping("/{id}")
    public PerfilUsuarioResponse obtenerPorId(
            @PathVariable Long id,
            Authentication auth) {

        return service.obtenerPorId(id, auth);
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('COMPRADOR')")
    public PerfilUsuarioResponse actualizarPerfil(
            @Valid @RequestBody ActualizarPerfilRequest request,
            Authentication auth) {

        return service.actualizarPerfil(request, auth);
    }

    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('COMPRADOR')")
    public void cambiarPassword(
            @Valid @RequestBody CambiarPasswordRequest request,
            Authentication auth) {

        service.cambiarPassword(request, auth);
    }



    // GET: Consultar dirección
    @GetMapping("/me/direccion")
    @PreAuthorize("hasRole('COMPRADOR')")
    public DireccionCompradorResponse obtenerMiDireccion(
            Authentication auth) {

        return service.obtenerMiDireccion(auth);
    }

    // PUT: Actualizar dirección
    @PutMapping("/me/direccion")
    @PreAuthorize("hasRole('COMPRADOR')")
    public DireccionCompradorResponse actualizarMiDireccion(
            @Valid @RequestBody DireccionCompradorRequest request,
            Authentication auth) {

        return service.actualizarMiDireccion(request, auth);
    }
}
