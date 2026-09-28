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
@Table(name = "vendedor", uniqueConstraints = {
        @UniqueConstraint(name = "uq_vendedor_ruc", columnNames = {"ruc"}),
        @UniqueConstraint(name = "uq_vendedor_usuario", columnNames = {"usuarioid"})
})
public class Vendedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idvendedor", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "nombretienda", nullable = false, length = 100)
    private String nombretienda;

    @Size(max = 500)
    @Column(name = "descripciontienda", length = 500)
    private String descripciontienda;

    @Size(max = 11)
    @NotNull
    @Column(name = "ruc", nullable = false, length = 11)
    private String ruc;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuarioid", nullable = false)
    private Usuario usuarioid;


}