package com.upc.webmarketar.services;

import com.upc.webmarketar.dtos.SalesDTO;

import java.time.LocalDate;

public interface ReportService {
    SalesDTO sales(LocalDate start, LocalDate end);

    OffersDTO offers(LocalDate start, LocalDate end);
}
