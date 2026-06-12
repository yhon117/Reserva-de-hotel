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
public class PagoDTO {
    private Long id;
    private Long reservaId;
    private String huespedNombre;
    private String habitacionNumero;
    private Double monto;
    private String metodoPago;
    private String estadoPago;
    private String codigoTransaccion;
    private LocalDateTime fechaPago;
    private String observaciones;
}
