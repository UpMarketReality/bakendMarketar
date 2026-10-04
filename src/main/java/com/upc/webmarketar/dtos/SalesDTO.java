package com.upc.webmarketar.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SalesDTO(
        LocalDate startDate,
        LocalDate endDate,
        long purchaseCount,
        long unitsSold,
        BigDecimal netSalesAmount,
        String currency,
        List<ProductSalesDTO> products) {}
