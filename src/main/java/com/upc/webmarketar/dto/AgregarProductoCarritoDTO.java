package com.upc.webmarketar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgregarProductoCarritoDTO {

    private Long idProducto;
    private Integer cantidad;
}