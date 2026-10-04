package com.upc.webmarketar.dtos;

import java.math.BigDecimal;

public record ProductSalesDTO(
        long productId, String name, long unitsSold, BigDecimal netSalesAmount) {}
