package com.protec.recervhotel.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Table(name = "factura_items")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FacturaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Factura factura;

    @NotNull
    @Column(nullable = false)
    private String descripcion;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double precioUnitario;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double total;
}
