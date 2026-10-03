package com.upc.webmarketar.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "encargo", indexes = {
        @Index(name = "ix_encargo_reporte", columnList = "fechacreacion")
}, uniqueConstraints = {
        @UniqueConstraint(name = "encargo_idsolicitud_key", columnNames = {"idsolicitud"}),
        @UniqueConstraint(name = "encargo_idcotizacion_key", columnNames = {"idcotizacion"})
})
public class Encargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idencargo", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcotizacion", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Cotizacionprototipo cotizacionprototipo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idsolicitud", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Solicitud solicitud;

    @Size(max = 30)
    @NotNull
    @ColumnDefault("'CONFIRMADO'")
    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fechacreacion", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime fechacreacion;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fechaactualizacion", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime fechaactualizacion;


}