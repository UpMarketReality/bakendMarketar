package com.upc.webmarketar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class RespuestaAgregarProductoDTO {

    private ItemCarritoDTO item;
    private BigDecimal total;
    private boolean nuevo;
}
