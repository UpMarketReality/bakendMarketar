package com.upc.webmarketar.dtos;

import jakarta.validation.constraints.NotNull;

public record ActiveRequest(@NotNull Boolean active) {}
