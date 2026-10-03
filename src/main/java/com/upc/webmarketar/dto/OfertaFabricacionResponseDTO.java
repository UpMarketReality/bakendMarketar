package com.upc.webmarketar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
public class OfertaFabricacionResponseDTO {

    private Long idOferta;
    private Long idSolicitud;
    private Long idVendedor;

    private BigDecimal montoTotal;
    private String moneda;

    private Integer plazo;
    private OffsetDateTime fechaVencimiento;

    private String condiciones;
    private String estado;
}