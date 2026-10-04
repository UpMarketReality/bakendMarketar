package com.upc.webmarketar.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class OfertaFabricacionRequestDTO {

    private BigDecimal monto;
    private String moneda;
    private Integer diasEntrega;
    private OffsetDateTime fechaVencimiento;
    private String condiciones;
}

