package com.protec.recervhotel.entities;

import com.protec.recervhotel.enums.EstadoHab;
import com.protec.recervhotel.enums.TipoHab;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotBlank
    @Column(nullable = false, unique = true)
    private String numero;

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer piso;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double precioNoche;

    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer capacidad;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoHab tipo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoHab estado;

    @OneToMany(mappedBy = "habitacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Reserva> reservas;
}
