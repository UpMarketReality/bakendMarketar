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
@Table(name = "boletaventa", indexes = {
        @Index(name = "ix_boletaventa_reporte", columnList = "fechaventa, estadoemision"),
        @Index(name = "ix_boletaventa_comprador", columnList = "idcomprador, fechaventa DESC")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_boletaventa_idempotencia", columnNames = {"idcomprador", "claveidempotencia"}),
        @UniqueConstraint(name = "uq_boletaventa_carrito", columnNames = {"idcarrito"})
})
public class Boletaventa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idboletaventa", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotNull
    @ColumnDefault("'SIMULADO'")
    @Column(name = "metodopago", nullable = false, length = 50)
    private String metodopago;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fechaventa", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime fechaventa;

    @Size(max = 50)
    @NotNull
    @ColumnDefault("'PENDIENTE'")
    @Column(name = "estadoemision", nullable = false, length = 50)
    private String estadoemision;

    @NotNull
    @Column(name = "preciototal", nullable = false, precision = 10, scale = 2)
    private BigDecimal preciototal;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcomprador", nullable = false)
    private Comprador idcomprador;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcarrito", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Carrito carrito;

    @Size(max = 200)
    @NotNull
    @Column(name = "claveidempotencia", nullable = false, length = 200)
    private String claveidempotencia;

    @Size(max = 64)
    @NotNull
    @Column(name = "huellaoperacion", nullable = false, length = 64)
    private String huellaoperacion;


}