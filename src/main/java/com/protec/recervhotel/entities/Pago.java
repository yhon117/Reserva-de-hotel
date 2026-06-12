package com.protec.recervhotel.entities;

import com.protec.recervhotel.enums.EstadoPago;
import com.protec.recervhotel.enums.MetodoPago;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Reserva reserva;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double monto;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPago metodoPago;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPago estadoPago;

    @Column(unique = true)
    private String codigoTransaccion;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaPago;

    private String observaciones;

    @PrePersist
    public void prePersist() {
        this.fechaPago = LocalDateTime.now();
    }
}
