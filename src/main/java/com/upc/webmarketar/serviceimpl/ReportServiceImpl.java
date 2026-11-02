package com.upc.webmarketar.serviceimpl;

import com.upc.webmarketar.dtos.OffersDTO;
import com.upc.webmarketar.dtos.SalesDTO;
import com.upc.webmarketar.exceptions.ApiException;
import com.upc.webmarketar.repositories.ReportRepository;
import com.upc.webmarketar.security.services.Identity;
import com.upc.webmarketar.services.ReportService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Service
@Transactional(readOnly = true)
@PreAuthorize("hasRole('VENDEDOR')")
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reports;
    private final Identity identity;

    public ReportServiceImpl(ReportRepository reports, Identity identity) {
        this.reports = reports;
        this.identity = identity;
    }

    private OffsetDateTime[] bounds(LocalDate start, LocalDate end) {
        if (start.isAfter(end) || end.equals(LocalDate.MAX))
            throw ApiException.bad("Período inválido");
        ZoneId lima = ZoneId.of("America/Lima");
        return new OffsetDateTime[] {
            start.atStartOfDay(lima).toOffsetDateTime(),
            end.plusDays(1).atStartOfDay(lima).toOffsetDateTime()
        };
    }

    @Override
    public SalesDTO sales(LocalDate start, LocalDate end) {
        var b = bounds(start, end);
        long seller = identity.seller();
        var totals = reports.sales(seller, b[0], b[1]);
        return new SalesDTO(
                start,
                end,
                totals.getPurchases(),
                totals.getUnits(),
                totals.getAmount(),
                "PEN",
                reports.products(seller, b[0], b[1]));
    }

    @Override
    public OffersDTO offers(LocalDate start, LocalDate end) {
        var b = bounds(start, end);
        long seller = identity.seller();
        var totals = reports.orders(seller, b[0], b[1]);
        return new OffersDTO(
                start,
                end,
                reports.issued(seller, b[0], b[1]),
                totals.getQuantity(),
                totals.getAmount(),
                "PEN");
    }
}
