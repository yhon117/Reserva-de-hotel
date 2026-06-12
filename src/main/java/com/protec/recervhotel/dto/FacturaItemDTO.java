package com.protec.recervhotel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaItemDTO {
    private Long id;
    private String descripcion;
    private Integer cantidad;
    private Double precioUnitario;
    private Double total;
}
