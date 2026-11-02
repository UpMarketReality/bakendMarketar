package com.upc.webmarketar.controllers;

import com.upc.webmarketar.dtos.OffersDTO;
import com.upc.webmarketar.dtos.SalesDTO;
import com.upc.webmarketar.services.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/seller/reports")
public class ReportController {
    private final ReportService s;

    public ReportController(ReportService s) {
        this.s = s;
    }

    @GetMapping("/sales")
    public SalesDTO sales(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return s.sales(startDate, endDate);
    }

    @GetMapping("/offers")
    public OffersDTO offers(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return s.offers(startDate, endDate);
    }
}
