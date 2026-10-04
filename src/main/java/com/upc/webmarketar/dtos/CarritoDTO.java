package com.upc.webmarketar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarritoDTO {

    private Long idCarrito;
    private List<ItemCarritoDTO> items;
    private BigDecimal total;
}
