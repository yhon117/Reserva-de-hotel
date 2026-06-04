package com.protec.recervhotel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaDTO {
    private Long id;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private Double total;
    private String estado;
    private LocalDateTime fechaCreacion;
    private Long usuarioId;
    private Long habitacionId;
    private String usuarioNombre;
    private String habitacionNumero;
}
