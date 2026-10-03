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
@Table(name = "solicitud")
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idsolicitud", nullable = false)
    private Long id;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fechasolicitud", nullable = false)
    private Instant fechasolicitud;

    @NotNull
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Size(max = 200)
    @NotNull
    @Column(name = "zonaentrega", nullable = false, length = 200)
    private String zonaentrega;

    @Size(max = 30)
    @NotNull
    @ColumnDefault("'PENDIENTE'")
    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idcomprador", nullable = false)
    private Comprador idcomprador;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idprototipoia", nullable = false)
    private Prototipoia idprototipoia;


}