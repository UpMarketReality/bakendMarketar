package com.upc.webmarketar.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CambiarPasswordRequest(
        @NotBlank
        String passwordActual,

        @NotBlank
        @Size(min = 12, max = 64)
        String passwordNueva
) {
}
