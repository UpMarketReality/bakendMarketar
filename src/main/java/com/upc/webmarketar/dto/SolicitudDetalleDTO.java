package com.upc.webmarketar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudDetalleDTO {

    private Long idSolicitud;

    private Long idPrototipo;
    private String prototipo;

    private Long idCategoria;
    private String categoria;

    private List<String> imagenes;

    private String especificaciones;

    private Integer cantidad;

    private String zonaEntrega;

    private String condicionesEntrega;

    private OffsetDateTime fechaSolicitud;

    private String estado;
}
