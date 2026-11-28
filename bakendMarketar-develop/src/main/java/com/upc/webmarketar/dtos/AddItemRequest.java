package com.upc.webmarketar.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddItemRequest(
        @NotNull @Positive Long productId, @NotNull @Positive Integer quantity) {}
