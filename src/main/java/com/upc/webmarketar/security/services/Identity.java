package com.upc.webmarketar.security.services;

import com.upc.webmarketar.exceptions.ApiException;
import com.upc.webmarketar.repositories.*;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Identity {
    private final UsuarioRepository users;
    private final CompradorRepository buyers;
    private final VendedorRepository sellers;

    public Identity(
            UsuarioRepository users, CompradorRepository buyers, VendedorRepository sellers) {
        this.users = users;
        this.buyers = buyers;
        this.sellers = sellers;
    }

    public long user() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            throw new ApiException(401, "Inicia sesi?n");
        }
        return users.findByCorreo(auth.getName())
                .orElseThrow(() -> new ApiException(401, "Inicia sesi?n"))
                .getId();
    }

    public long buyer() {
        return buyers.findByUsuarioid_Id(user()).orElseThrow(ApiException::missing).getId();
    }

    public long seller() {
        return sellers.findByUsuarioid_Id(user()).orElseThrow(ApiException::missing).getId();
    }
}

