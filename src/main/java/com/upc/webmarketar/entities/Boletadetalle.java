package com.upc.webmarketar.entities;

import com.upc.webmarketar.entities.Boletaventa;
import com.upc.webmarketar.entities.Cotizacionprototipo;
import com.upc.webmarketar.entities.Producto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "boletadetalle", indexes = {
        @Index(name = "ix_boletadetalle_boleta", columnList = "idboletaventa"),
        @Index(name = "ix_boletadetalle_producto", columnList = "idproducto")
})
public class Boletadetalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idboletadetalle", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @NotNull
    @Column(name = "preciounitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal preciounitario;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "descuento", nullable = false, precision = 10, scale = 2)
    private BigDecimal descuento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "idboletaventa", nullable = false)
    private Boletaventa idboletaventa;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idproducto", nullable = false)
    private Producto idproducto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcotizacion")
    private Cotizacionprototipo idcotizacion;


}