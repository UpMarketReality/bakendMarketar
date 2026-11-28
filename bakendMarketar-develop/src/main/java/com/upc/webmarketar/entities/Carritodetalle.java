package com.upc.webmarketar.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "carritodetalle", indexes = {
        @Index(name = "ix_carritodetalle_producto", columnList = "idproducto")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_carrito_producto", columnNames = {"idcarrito", "idproducto"})
})
public class Carritodetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcarritodetalle", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @NotNull
    @Column(name = "preciounitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal preciounitario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "idcarrito", nullable = false)
    private Carrito idcarrito;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idproducto", nullable = false)
    private Producto idproducto;


}