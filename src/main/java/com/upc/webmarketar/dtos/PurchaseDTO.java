package com.upc.webmarketar.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record PurchaseDTO(
        long purchaseId,
        OffsetDateTime date,
        List<PurchaseItemDTO> items,
        BigDecimal total,
        String method,
        String status) {}
