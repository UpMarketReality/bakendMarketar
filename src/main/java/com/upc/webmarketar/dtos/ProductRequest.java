package com.upc.webmarketar.dtos;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record ProductRequest(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 1000) String description,
        @NotNull @Positive Long categoryId,
        @NotNull @DecimalMin(value = "0", inclusive = false) @Digits(integer = 8, fraction = 2)
                BigDecimal unitPrice,
        @DecimalMin(value = "0", inclusive = false) @Digits(integer = 8, fraction = 2)
                BigDecimal wholesalePrice,
        @NotBlank @Size(max = 150) String dimensions,
        @NotBlank @Size(max = 100) String material,
        @NotBlank @Size(max = 50) String color,
        @NotNull @PositiveOrZero Integer stock,
        List<Long> retainedImageIds) {}
