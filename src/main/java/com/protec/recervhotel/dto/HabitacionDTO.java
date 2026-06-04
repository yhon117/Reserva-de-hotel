package com.protec.recervhotel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitacionDTO {
    private Long id;
    private String numero;
    private Integer piso;
    private Double precioNoche;
    private Integer capacidad;
    private String tipo;
    private String estado;
}
