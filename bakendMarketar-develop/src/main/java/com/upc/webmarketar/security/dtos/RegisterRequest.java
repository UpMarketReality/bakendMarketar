package com.upc.webmarketar.security.dtos;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 150) String email,
        @NotNull @Size(min = 8, max = 64) String password,
        @Pattern(regexp = "COMPRADOR|VENDEDOR") @NotNull String role,
        @Size(max = 300) String address,
        @Size(max = 100) String storeName,
        @Size(max = 11) String ruc,
        @Size(max = 500) String storeDescription) {}
