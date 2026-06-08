package com.protec.recervhotel.controller;

import com.protec.recervhotel.dto.*;
import com.protec.recervhotel.enums.Rol;
import com.protec.recervhotel.security.CustomUserDetailsService;
import com.protec.recervhotel.security.JwtTokenProvider;
import com.protec.recervhotel.service.UsuarioService;
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

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private final UsuarioDTO userDTO = UsuarioDTO.builder()
            .id(1L).nombre("Juan").email("juan@test.com")
            .telefono("123456789").rol(Rol.USER).build();

    private final LoginResponseDTO loginDTO = LoginResponseDTO.builder()
            .token("token123").id(1L).nombre("Juan")
            .email("juan@test.com").rol(Rol.USER).build();

    @Test
    void registro_Returns201() throws Exception {
        when(usuarioService.registrar(any(UsuarioRegistroDTO.class))).thenReturn(userDTO);

        mockMvc.perform(post("/api/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Juan","email":"juan@test.com",
                                "password":"pass123","telefono":"123456789"}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    void login_Returns200() throws Exception {
        when(usuarioService.login(any(UsuarioLoginDTO.class))).thenReturn(loginDTO);

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"juan@test.com\",\"password\":\"pass123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listarTodos_AsAdmin_Returns200() throws Exception {
        when(usuarioService.listarTodos()).thenReturn(List.of(userDTO));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerPorId_AsAdmin_Returns200() throws Exception {
        when(usuarioService.obtenerPorId(1L)).thenReturn(userDTO);

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminar_AsAdmin_Returns204() throws Exception {
        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crearAdmin_AsAdmin_Returns201() throws Exception {
        UsuarioDTO adminDTO = UsuarioDTO.builder().id(2L).nombre("Admin")
                .email("admin@test.com").rol(Rol.ADMIN).build();
        when(usuarioService.crearPorAdmin(any(UsuarioRegistroDTO.class))).thenReturn(adminDTO);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Admin","email":"admin@test.com",
                                "password":"pass","rol":"ADMIN"}"""))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizar_AsAdmin_Returns200() throws Exception {
        when(usuarioService.actualizar(eq(1L), any(UsuarioActualizacionDTO.class)))
                .thenReturn(userDTO);

        mockMvc.perform(put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Juan","email":"juan@test.com",
                                "telefono":"987654321"}"""))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void cambiarRol_AsAdmin_Returns200() throws Exception {
        UsuarioDTO adminDTO = UsuarioDTO.builder().id(1L).nombre("Juan")
                .email("juan@test.com").rol(Rol.ADMIN).build();
        when(usuarioService.cambiarRol(eq(1L), any(Rol.class))).thenReturn(adminDTO);

        mockMvc.perform(put("/api/usuarios/1/rol")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"ADMIN\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("ADMIN"));
    }
}
