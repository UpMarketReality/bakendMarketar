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
@Table(name = "producto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idproducto", nullable = false)
    private Long id;

    @Size(max = 150)
    @NotNull
    @Column(name = "nombreproducto", nullable = false, length = 150)
    private String nombreproducto;

    @Size(max = 1000)
    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @NotNull
    @Column(name = "preciounidad", nullable = false, precision = 10, scale = 2)
    private BigDecimal preciounidad;

    @Column(name = "precioxmayor", precision = 10, scale = 2)
    private BigDecimal precioxmayor;

    @Size(max = 150)
    @Column(name = "dimensiones", length = 150)
    private String dimensiones;

    @Size(max = 100)
    @Column(name = "material", length = 100)
    private String material;

    @Size(max = 50)
    @Column(name = "color", length = 50)
    private String color;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fechapublicacion", nullable = false)
    private Instant fechapublicacion;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Size(max = 30)
    @NotNull
    @ColumnDefault("'ACTIVO'")
    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idvendedor", nullable = false)
    private Vendedor idvendedor;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcategoria", nullable = false)
    private Categoriaproducto idcategoria;


}