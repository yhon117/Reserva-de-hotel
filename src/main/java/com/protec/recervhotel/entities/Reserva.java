package com.protec.recervhotel.entities;

import com.protec.recervhotel.enums.Estado;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reserva")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDate fechaEntrada;

    @NotNull
    @Column(nullable = false)
    private LocalDate fechaSalida;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double total;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habitacion_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Habitacion habitacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
