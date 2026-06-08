package com.protec.recervhotel.controller;

import com.protec.recervhotel.dto.HabitacionCreacionDTO;
import com.protec.recervhotel.dto.HabitacionDTO;
import com.protec.recervhotel.security.CustomUserDetailsService;
import com.protec.recervhotel.security.JwtTokenProvider;
import com.protec.recervhotel.service.HabitacionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HabitacionController.class)
class HabitacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HabitacionService habitacionService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private final HabitacionDTO habDTO = HabitacionDTO.builder()
            .id(1L).numero("101").piso(1).precioNoche(100.0)
            .capacidad(2).tipo("SIMPLE").estado("DISPONIBLE").build();

    @Test
    @WithMockUser(roles = "ADMIN")
    void crear_AsAdmin_Returns201() throws Exception {
        when(habitacionService.crear(any(HabitacionCreacionDTO.class))).thenReturn(habDTO);

        mockMvc.perform(post("/api/habitaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"numero":"101","piso":1,"precioNoche":100.0,
                                "capacidad":2,"tipo":"SIMPLE","estado":"DISPONIBLE"}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value("101"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listarTodas_Returns200() throws Exception {
        when(habitacionService.listarTodas()).thenReturn(List.of(habDTO));

        mockMvc.perform(get("/api/habitaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerPorId_Returns200() throws Exception {
        when(habitacionService.obtenerPorId(1L)).thenReturn(habDTO);

        mockMvc.perform(get("/api/habitaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value("101"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizar_AsAdmin_Returns200() throws Exception {
        when(habitacionService.actualizar(eq(1L), any(HabitacionCreacionDTO.class)))
                .thenReturn(habDTO);

        mockMvc.perform(put("/api/habitaciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"numero":"101","piso":1,"precioNoche":100.0,
                                "capacidad":2,"tipo":"SIMPLE","estado":"DISPONIBLE"}"""))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminar_AsAdmin_Returns204() throws Exception {
        mockMvc.perform(delete("/api/habitaciones/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarDisponiblesPorTipo_Returns200() throws Exception {
        when(habitacionService.buscarDisponiblesPorTipo("SIMPLE"))
                .thenReturn(List.of(habDTO));

        mockMvc.perform(get("/api/habitaciones/disponibles?tipo=SIMPLE"))
                .andExpect(status().isOk());
    }
}
