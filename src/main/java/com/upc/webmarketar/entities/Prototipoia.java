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
@Table(name = "prototipoia")
public class Prototipoia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idprototipoia", nullable = false)
    private Long id;


    @Column(name = "nombreprototipo", nullable = false, length = 150)
    private String nombreprototipo;


    @Column(name = "prompt", nullable = false, length = 2000)
    private String prompt;


    @Column(name = "especificaciones", length = 2000)
    private String especificaciones;


    @Column(name = "modeloia", length = 100)
    private String modeloia;


    @Column(name = "mensajeerrorseguro", length = 300)
    private String mensajeerrorseguro;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fechaactualizacion", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime fechaactualizacion;


    @ColumnDefault("'PENDIENTE'")
    @Column(name = "estadogeneracion", nullable = false, length = 30)
    private String estadogeneracion;


    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fechacreacion", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime fechacreacion;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcomprador", nullable = false)
    private Comprador comprador;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcategoriaprototipo", nullable = false)
    private Categoriaprototipo idcategoriaprototipo;


}