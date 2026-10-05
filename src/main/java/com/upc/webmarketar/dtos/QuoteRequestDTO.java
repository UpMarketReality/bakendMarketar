package com.upc.webmarketar.dtos;

import java.time.OffsetDateTime;
import java.util.List;

public record QuoteRequestDTO(
        long requestId,
        long prototypeId,
        String prototype,
        long categoryId,
        int quantity,
        String deliveryAddress,
        String deliveryZone,
        String deliveryConditions,
        String specifications,
        List<String> images,
        String status,
        OffsetDateTime createdAt,
        long offerCount) {}

