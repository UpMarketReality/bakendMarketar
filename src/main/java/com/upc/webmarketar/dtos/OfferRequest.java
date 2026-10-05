package com.upc.webmarketar.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record OfferRequest(
        @NotNull @DecimalMin(value = "0", inclusive = false) @Digits(integer = 8, fraction = 2)
        BigDecimal amount,
        @NotNull @Pattern(regexp = "PEN") String currency,
        @NotNull @Positive Integer deliveryDays,
        @NotNull OffsetDateTime expiresAt,
        @NotBlank @Size(max = 1000) String conditions) {}