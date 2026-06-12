package com.protec.recervhotel.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facturas")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Reserva reserva;

    @Column(nullable = false, unique = true)
    private String numeroFactura;

    @Column(nullable = false)
    private Double subtotal;

    @Column(nullable = false)
    private Double iva;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaEmision;

    @Column(nullable = false)
    @Builder.Default
    private Boolean pagada = false;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<FacturaItem> items = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.fechaEmision = LocalDateTime.now();
    }
}
