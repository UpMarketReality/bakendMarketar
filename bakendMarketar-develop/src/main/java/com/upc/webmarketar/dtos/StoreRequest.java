package com.upc.webmarketar.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StoreRequest(
        @NotBlank @Size(max = 100) String storeName, @Size(max = 500) String storeDescription) {}
