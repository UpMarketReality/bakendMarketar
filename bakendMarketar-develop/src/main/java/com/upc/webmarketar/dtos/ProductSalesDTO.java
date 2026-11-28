package com.upc.webmarketar.dtos;

import java.math.BigDecimal;

public record ProductSalesDTO(
        Long productId,
        String name,
        Long unitsSold,
        BigDecimal netSalesAmount) {

    // Constructor que invoca Hibernate cuando el cálculo aritmético da Long
    public ProductSalesDTO(Long productId, String name, Long unitsSold, Long netSalesAmount) {
        this(productId, name, unitsSold, netSalesAmount != null ? BigDecimal.valueOf(netSalesAmount) : BigDecimal.ZERO);
    }
}
