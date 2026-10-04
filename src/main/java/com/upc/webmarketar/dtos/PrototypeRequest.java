package com.upc.webmarketar.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Map;

public record PrototypeRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull @Positive Long categoryId,
        @NotBlank @Size(max = 2000) String prompt,
        @NotEmpty Map<String, Object> specifications) {}
