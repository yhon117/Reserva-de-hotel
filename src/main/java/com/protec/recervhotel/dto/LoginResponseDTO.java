package com.protec.recervhotel.dto;

import com.protec.recervhotel.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private String token;
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private Rol rol;
}
