package com.upc.webmarketar.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateQuoteRequest(
        @NotNull @Positive Long prototypeId,
        @NotNull @Positive Integer quantity,
        @NotBlank @Size(max = 300) String deliveryAddress,
        @NotBlank @Size(max = 150) String deliveryZone,
        @NotBlank @Size(max = 1000) String deliveryConditions) {}
