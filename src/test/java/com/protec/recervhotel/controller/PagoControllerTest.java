package com.protec.recervhotel.controller;

import com.protec.recervhotel.dto.PagoCreacionDTO;
import com.protec.recervhotel.dto.PagoDTO;
import com.protec.recervhotel.security.CustomUserDetailsService;
import com.protec.recervhotel.security.JwtTokenProvider;
import com.protec.recervhotel.service.PagoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagoController.class)
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PagoService pagoService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private final PagoDTO pagoDTO = PagoDTO.builder()
            .id(1L).reservaId(1L).monto(400.0)
            .metodoPago("EFECTIVO").estadoPago("PAGADO")
            .build();

    @Test
    @WithMockUser
    void registrarPago_Returns201() throws Exception {
        when(pagoService.registrarPago(any())).thenReturn(pagoDTO);

        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reservaId\":1,\"monto\":400.0,\"metodoPago\":\"EFECTIVO\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerPorId_AsAdmin_ReturnsPago() throws Exception {
        when(pagoService.obtenerPorId(1L)).thenReturn(pagoDTO);

        mockMvc.perform(get("/api/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "RECEPCIONISTA")
    void listarTodos_AsRecepcionista_ReturnsList() throws Exception {
        when(pagoService.listarTodos()).thenReturn(List.of(pagoDTO));

        mockMvc.perform(get("/api/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listarPorReserva_ReturnsFiltered() throws Exception {
        when(pagoService.listarPorReserva(1L)).thenReturn(List.of(pagoDTO));

        mockMvc.perform(get("/api/pagos?reservaId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reservaId").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void registrarPago_AsUser_Returns201() throws Exception {
        when(pagoService.registrarPago(any())).thenReturn(pagoDTO);

        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reservaId\":1,\"monto\":400.0,\"metodoPago\":\"EFECTIVO\"}"))
                .andExpect(status().isCreated());
    }
}
