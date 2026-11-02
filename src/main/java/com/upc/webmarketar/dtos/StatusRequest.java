package com.upc.webmarketar.dtos;

import jakarta.validation.constraints.NotBlank;

public record StatusRequest(@NotBlank String status) {}
