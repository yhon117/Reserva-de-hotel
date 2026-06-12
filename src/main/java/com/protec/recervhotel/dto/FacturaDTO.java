package com.protec.recervhotel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaDTO {
    private Long id;
    private String numeroFactura;
    private Long reservaId;
    private String huespedNombre;
    private String huespedEmail;
    private String habitacionNumero;
    private String habitacionTipo;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private Long noches;
    private Double subtotal;
    private Double iva;
    private Double total;
    private Boolean pagada;
    private LocalDateTime fechaEmision;
    private List<FacturaItemDTO> items;
}
