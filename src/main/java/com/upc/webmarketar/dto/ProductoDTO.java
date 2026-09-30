package com.upc.webmarketar.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductoDTO {
    private String nombreproducto;
    private String descripcion;
    private BigDecimal preciounidad;
    private BigDecimal precioxmayor;
    private String dimensiones;
    private String material;
    private String color;
    private Instant fechapublicacion;
    private Integer stock;
    private String estado;

    private long idVendedor;

    private long idcategoria;
}
