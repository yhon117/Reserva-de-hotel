package com.protec.recervhotel.entitys;

import com.protec.recervhotel.emun.EstadoHab;
import com.protec.recervhotel.emun.TipoHab;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "habitaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Column(nullable = false)
    private Integer piso;

    @Column(nullable = false)
    private Double precioNoche;

    @Column(nullable = false)
    private Integer capacidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoHab tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoHab estado;

    // Relación inversa: Una Habitacion tiene muchas Reservas
    @OneToMany(mappedBy = "habitacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Reserva> reservas;
}