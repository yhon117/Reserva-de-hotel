package com.protec.recervhotel.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitacionCreacionDTO {

    @NotBlank
    private String numero;

    @NotNull
    @Min(0)
    private Integer piso;

    @NotNull
    @Positive
    private Double precioNoche;

    @NotNull
    @Min(1)
    private Integer capacidad;

    @NotBlank
    private String tipo;

    @NotBlank
    private String estado;
}
