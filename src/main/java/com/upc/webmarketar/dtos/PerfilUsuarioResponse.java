package com.upc.webmarketar.dtos;

public record PerfilUsuarioResponse(
        Long id,
        String nombre,
        String correo,
        String telefono,
        String documento
) {
}
