package com.upc.webmarketar.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "categoriaproducto", uniqueConstraints = {
        @UniqueConstraint(name = "uq_categoriaproducto_nombre", columnNames = {"nombre"})
})
public class Categoriaproducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcategoria", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Size(max = 300)
    @Column(name = "descripcion", length = 300)
    private String descripcion;

    @Size(max = 50)
    @Column(name = "tipo", length = 50)
    private String tipo;


}