package com.upc.webmarketar.dtos;

import java.math.BigDecimal;

public record PurchaseItemDTO(
        long productId,
        String name,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal discount,
        BigDecimal subtotal) {}
