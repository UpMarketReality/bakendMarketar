package com.upc.webmarketar.controllers;

import com.upc.webmarketar.dtos.ActualizarPerfilRequest;
import com.upc.webmarketar.dtos.CambiarPasswordRequest;
import com.upc.webmarketar.dtos.PerfilUsuarioResponse;
import com.upc.webmarketar.serviceimpl.UsuarioPerfilService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UsuarioPerfilService service;

    @GetMapping("/usuario/me")
    public PerfilUsuarioResponse obtenerMiPerfil(
            Authentication auth) {

        return service.obtenerMiPerfil(auth);
    }

    @GetMapping("/usuario/{id}")
    public PerfilUsuarioResponse obtenerPorId(
            @PathVariable Long id,
            Authentication auth) {

        return service.obtenerPorId(id, auth);
    }

    @PutMapping("/usuario/me")
    public PerfilUsuarioResponse actualizarPerfil(
            @Valid @RequestBody ActualizarPerfilRequest request,
            Authentication auth) {

        return service.actualizarPerfil(request, auth);
    }

    @PatchMapping("/usuario/me/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiarPassword(
            @Valid @RequestBody CambiarPasswordRequest request,
            Authentication auth) {

        service.cambiarPassword(request, auth);
    }
}
