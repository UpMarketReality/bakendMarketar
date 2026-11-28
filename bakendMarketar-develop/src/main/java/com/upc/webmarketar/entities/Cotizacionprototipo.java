package com.upc.webmarketar.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "cotizacionprototipo", indexes = {
        @Index(name = "ix_cotizacion_vigencia", columnList = "estado, fechavencimiento"),
        @Index(name = "ix_cotizacion_reporte", columnList = "idvendedor, fecha")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_cotizacion_solicitud", columnNames = {"idcotizacion", "idsolicitud"}),
        @UniqueConstraint(name = "uq_cotizacion_vendedor_solicitud", columnNames = {"idsolicitud", "idvendedor"})
})
public class Cotizacionprototipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcotizacion", nullable = false)
    private Long id;

    @Size(max = 30)
    @NotNull
    @ColumnDefault("'PENDIENTE'")
    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Size(max = 1000)
    @NotNull
    @Column(name = "comentario", nullable = false, length = 1000)
    private String comentario;

    @Size(max = 3)
    @NotNull
    @ColumnDefault("'PEN'")
    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fecha", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime fecha;

    @NotNull
    @Column(name = "fechavencimiento", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime fechavencimiento;

    @NotNull
    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @NotNull
    @Column(name = "tiempoentrega", nullable = false)
    private Integer tiempoentrega;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idsolicitud", nullable = false)
    private Solicitud idsolicitud;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idvendedor", nullable = false)
    private Vendedor idvendedor;


}