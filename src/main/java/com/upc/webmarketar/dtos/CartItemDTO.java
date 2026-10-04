package com.upc.webmarketar.dtos;

import java.math.BigDecimal;

public record CartItemDTO(
        long itemId, ProductDTO product, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {}

