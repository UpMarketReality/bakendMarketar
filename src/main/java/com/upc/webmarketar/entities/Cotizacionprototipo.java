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
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "cotizacionprototipo")
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
    @Column(name = "comentario", length = 1000)
    private String comentario;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fecha", nullable = false)
    private Instant fecha;

    @Column(name = "fechavencimiento")
    private Instant fechavencimiento;

    @NotNull
    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "tiempoentrega")
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