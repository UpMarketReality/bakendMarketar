package com.upc.webmarketar.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderDTO(
        long orderId,
        long requestId,
        long offerId,
        long sellerId,
        String storeName,
        long prototypeId,
        String status,
        BigDecimal amount,
        String currency,
        int deliveryDays,
        int quantity,
        String deliveryAddress,
        String conditions,
        String specifications,
        List<String> images,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {}
