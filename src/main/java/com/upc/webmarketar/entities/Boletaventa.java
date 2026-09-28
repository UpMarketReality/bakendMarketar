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
@Table(name = "boletaventa")
public class Boletaventa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idboletaventa", nullable = false)
    private Long id;

    @Size(max = 50)
    @Column(name = "metodopago", length = 50)
    private String metodopago;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fechaventa", nullable = false)
    private Instant fechaventa;

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


}