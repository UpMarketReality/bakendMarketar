package com.upc.webmarketar.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudResumenDTO {

    private Long idSolicitud;

    private Long idPrototipo;
    private String prototipo;

    private Long idCategoria;
    private String categoria;

    private Integer cantidad;
    private String zonaEntrega;

    private Instant fecha;
    private String estado;
}
