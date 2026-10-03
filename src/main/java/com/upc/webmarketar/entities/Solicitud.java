package com.upc.webmarketar.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "solicitud", indexes = {
        @Index(name = "ix_solicitud_abiertas",
                columnList = "estado, fechasolicitud DESC"),
        @Index(name = "ix_solicitud_comprador",
                columnList = "idcomprador, fechasolicitud DESC, idsolicitud DESC")})
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idsolicitud", nullable = false)
    private Long id;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fechasolicitud", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime fechasolicitud;

    @NotNull
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Size(max = 200)
    @NotNull
    @Column(name = "zonaentrega", nullable = false, length = 200)
    private String zonaentrega;

    @Size(max = 30)
    @NotNull
    @ColumnDefault("'EN_COTIZACION'")
    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @NotNull
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Size(max = 300)
    @NotNull
    @Column(name = "direccionentrega", nullable = false, length = 300)
    private String direccionentrega;

    @Size(max = 150)
    @NotNull
    @Column(name = "zonaentrega", nullable = false, length = 150)
    private String zonaentrega;

    @Size(max = 1000)
    @NotNull
    @Column(name = "condicionesentrega", nullable = false, length = 1000)
    private String condicionesentrega;

    @NotNull
    @Column(name = "especificacionesacordadas", nullable = false, columnDefinition = "text")
    private String especificacionesacordadas;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "imagenesreferencia", nullable = false, columnDefinition = "jsonb")
    private List<String> imagenesreferencia;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fechaactualizacion", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime fechaactualizacion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcomprador", nullable = false)
    private Comprador idcomprador;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idprototipoia", nullable = false)
    private Prototipoia prototipoia;


}