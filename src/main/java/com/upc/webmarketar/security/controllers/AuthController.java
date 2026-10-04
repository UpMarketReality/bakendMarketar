package com.upc.webmarketar.security.controllers;

import com.upc.webmarketar.dtos.*;
import com.upc.webmarketar.security.dtos.*;
import com.upc.webmarketar.services.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@io.swagger.v3.oas.annotations.security.SecurityRequirements
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService s) {
        service = s;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest r) {
        return ResponseEntity.status(201).body(service.register(r));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginDTO> login(@Valid @RequestBody LoginRequest r) {
        var v = service.login(r);
        return ResponseEntity.ok().header("Authorization", "Bearer " + v.jwt()).body(v);
    }
}