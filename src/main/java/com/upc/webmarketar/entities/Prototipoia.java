package com.upc.webmarketar.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

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

    @Size(max = 150)
    @NotNull
    @Column(name = "nombreprototipo", nullable = false, length = 150)
    private String nombreprototipo;

    @Size(max = 2000)
    @NotNull
    @Column(name = "prompt", nullable = false, length = 2000)
    private String prompt;

    @Size(max = 2000)
    @Column(name = "especificaciones", length = 2000)
    private String especificaciones;

    @Size(max = 100)
    @Column(name = "modeloia", length = 100)
    private String modeloia;

    @Size(max = 30)
    @NotNull
    @ColumnDefault("'PENDIENTE'")
    @Column(name = "estadogeneracion", nullable = false, length = 30)
    private String estadogeneracion;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fechacreacion", nullable = false)
    private Instant fechacreacion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcomprador", nullable = false)
    private Comprador comprador;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcategoriaprototipo", nullable = false)
    private Categoriaprototipo idcategoriaprototipo;


}