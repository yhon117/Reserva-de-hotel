package com.protec.recervhotel.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaCreacionDTO {

    @NotNull
    private LocalDate fechaEntrada;

    @NotNull
    private LocalDate fechaSalida;

    @NotNull
    private Long usuarioId;

    @NotNull
    private Long habitacionId;
}
