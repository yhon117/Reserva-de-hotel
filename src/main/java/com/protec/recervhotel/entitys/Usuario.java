package com.protec.recervhotel.entitys;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Entity
@Table(name = "usurio")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    private String telefono;


    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude   // Evita recursión infinita en toString()
    @EqualsAndHashCode.Exclude
    private List<Reserva> reservas;
}
