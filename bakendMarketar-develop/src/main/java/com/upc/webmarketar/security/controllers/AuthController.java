package com.upc.webmarketar.security.controllers;

import com.upc.webmarketar.security.dtos.LoginDTO;
import com.upc.webmarketar.security.dtos.LoginRequest;
import com.upc.webmarketar.security.dtos.RegisterRequest;
import com.upc.webmarketar.security.dtos.UserDTO;
import com.upc.webmarketar.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
