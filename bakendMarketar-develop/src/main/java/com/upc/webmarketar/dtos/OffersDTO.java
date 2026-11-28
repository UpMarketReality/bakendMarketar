package com.upc.webmarketar.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OffersDTO(
        LocalDate startDate,
        LocalDate endDate,
        long offersIssued,
        long ordersConfirmed,
        BigDecimal confirmedOrdersAmount,
        String currency) {}
