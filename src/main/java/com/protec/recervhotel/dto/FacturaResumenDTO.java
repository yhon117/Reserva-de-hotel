package com.protec.recervhotel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaResumenDTO {
    private Long id;
    private String numeroFactura;
    private Long reservaId;
    private String huespedNombre;
    private String habitacionNumero;
    private Double total;
    private Boolean pagada;
    private LocalDateTime fechaEmision;
}
