package com.protec.recervhotel.dto;

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
public class PagoCreacionDTO {

    @NotNull
    private Long reservaId;

    @NotNull
    @Positive
    private Double monto;

    @NotBlank
    private String metodoPago;

    private String observaciones;
}
