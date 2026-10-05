package com.upc.webmarketar.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record OfferDTO(
        long offerId,
        long requestId,
        long sellerId,
        String storeName,
        BigDecimal amount,
        String currency,
        int deliveryDays,
        OffsetDateTime expiresAt,
        String conditions,
        String status,
        boolean isExpired) {}
