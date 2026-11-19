package com.upc.webmarketar.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ActualizarPerfilRequest(
        @NotBlank
        @Size(min = 3, max = 100)
        String nombre,

        @NotBlank
        @Pattern(regexp = "[0-9]{9}")
        String telefono,

        @NotBlank
        @Pattern(regexp = "[A-Za-z0-9]{8,12}")
        String documento
) {
}
