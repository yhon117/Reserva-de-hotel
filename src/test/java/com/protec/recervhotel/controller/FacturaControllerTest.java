package com.protec.recervhotel.controller;

import com.protec.recervhotel.dto.FacturaDTO;
import com.protec.recervhotel.dto.FacturaResumenDTO;
import com.protec.recervhotel.security.CustomUserDetailsService;
import com.protec.recervhotel.security.JwtTokenProvider;
import com.protec.recervhotel.service.FacturaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacturaController.class)
class FacturaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacturaService facturaService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private final FacturaDTO facturaDTO = FacturaDTO.builder()
            .id(1L).numeroFactura("FAC-001").reservaId(1L)
            .huespedNombre("Juan").total(357.0).pagada(false)
            .build();

    private final FacturaResumenDTO resumenDTO = FacturaResumenDTO.builder()
            .id(1L).numeroFactura("FAC-001").reservaId(1L)
            .huespedNombre("Juan").total(357.0).pagada(false)
            .build();

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerPorId_AsAdmin_ReturnsFactura() throws Exception {
        when(facturaService.obtenerPorId(1L)).thenReturn(facturaDTO);

        mockMvc.perform(get("/api/facturas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroFactura").value("FAC-001"));
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    void obtenerPorReserva_AsRecepcionista_ReturnsFactura() throws Exception {
        when(facturaService.obtenerPorReservaId(1L)).thenReturn(facturaDTO);

        mockMvc.perform(get("/api/facturas/por-reserva/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservaId").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listarTodas_ReturnsList() throws Exception {
        when(facturaService.listarTodas()).thenReturn(List.of(resumenDTO));

        mockMvc.perform(get("/api/facturas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numeroFactura").value("FAC-001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void generarPdf_ReturnsPdf() throws Exception {
        when(facturaService.generarPdf(1L)).thenReturn(new byte[]{37, 80, 68, 70});

        mockMvc.perform(get("/api/facturas/1/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "attachment; filename=factura-1.pdf"));
    }


}
