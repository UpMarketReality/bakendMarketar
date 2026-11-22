package com.upc.webmarketar.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DireccionCompradorRequest(
        @NotBlank
        @Size(max = 180)
        String calle,

        @NotBlank
        @Size(max = 60)
        String region,

        @NotBlank
        @Size(max = 60)
        String distrito,

        @Size(max = 200)
        String referencia
) {
}
