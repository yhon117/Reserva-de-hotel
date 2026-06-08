package com.protec.recervhotel.controller;

import com.protec.recervhotel.dto.ReservaCreacionDTO;
import com.protec.recervhotel.dto.ReservaDTO;
import com.protec.recervhotel.security.CustomUserDetailsService;
import com.protec.recervhotel.security.JwtTokenProvider;
import com.protec.recervhotel.service.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservaController.class)
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservaService reservaService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private final ReservaDTO reservaDTO = ReservaDTO.builder()
            .id(1L).fechaEntrada(LocalDate.now().plusDays(1))
            .fechaSalida(LocalDate.now().plusDays(3))
            .total(200.0).estado("CONFIRMADA")
            .usuarioId(1L).habitacionId(1L)
            .usuarioNombre("Juan").habitacionNumero("101")
            .build();

    @Test
    @WithMockUser(roles = "ADMIN")
    void crear_Returns201() throws Exception {
        when(reservaService.crear(any(ReservaCreacionDTO.class))).thenReturn(reservaDTO);

        mockMvc.perform(post("/api/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fechaEntrada":"2026-06-10","fechaSalida":"2026-06-12",
                                "usuarioId":1,"habitacionId":1}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(200.0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void cancelar_Returns200() throws Exception {
        ReservaDTO cancelled = ReservaDTO.builder().id(1L).estado("CANCELADA").build();
        when(reservaService.cancelar(1L)).thenReturn(cancelled);

        mockMvc.perform(put("/api/reservas/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerPorId_Returns200() throws Exception {
        when(reservaService.obtenerPorId(1L)).thenReturn(reservaDTO);

        mockMvc.perform(get("/api/reservas/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listarTodas_Returns200() throws Exception {
        when(reservaService.listarTodas()).thenReturn(List.of(reservaDTO));

        mockMvc.perform(get("/api/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listarPorHabitacion_Returns200() throws Exception {
        when(reservaService.listarPorHabitacion(1L)).thenReturn(List.of(reservaDTO));

        mockMvc.perform(get("/api/reservas?habitacionId=1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void estadisticasOcupadas_Returns200() throws Exception {
        when(reservaService.contarOcupadasEnFecha(any(LocalDate.class))).thenReturn(5L);

        mockMvc.perform(get("/api/reservas/estadisticas/ocupadas?fecha=2026-06-08"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void ingresosPorMes_Returns200() throws Exception {
        when(reservaService.ingresosPorMes())
                .thenReturn(List.of(Map.of("mes", 6, "anio", 2026, "total", 5000.0)));

        mockMvc.perform(get("/api/reservas/estadisticas/ingresos-por-mes"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void ocupacionPorMes_Returns200() throws Exception {
        when(reservaService.ocupacionAgrupadaPorMes())
                .thenReturn(List.of(Map.of("mes", 6, "anio", 2026, "cantidad", 10)));

        mockMvc.perform(get("/api/reservas/estadisticas/ocupacion-por-mes"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void tendencia_Returns200() throws Exception {
        when(reservaService.tendenciaDesde(any(LocalDate.class)))
                .thenReturn(List.of(Map.of("fecha", LocalDate.now(), "cantidad", 3)));

        mockMvc.perform(get("/api/reservas/estadisticas/tendencia?desde=2026-06-01"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void topHabitaciones_Returns200() throws Exception {
        when(reservaService.habitacionesMasReservadas())
                .thenReturn(List.of(Map.of("habitacionId", 1L, "numero", "101", "total", 10L)));

        mockMvc.perform(get("/api/reservas/estadisticas/habitaciones-mas-reservadas"))
                .andExpect(status().isOk());
    }
}
