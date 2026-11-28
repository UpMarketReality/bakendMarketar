package com.upc.webmarketar.dtos;

import jakarta.validation.constraints.*;

import java.util.Map;

public record PrototypeRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull @Positive Long categoryId,
        @NotBlank @Size(max = 2000) String prompt,
        @NotEmpty Map<String, Object> specifications) {}
