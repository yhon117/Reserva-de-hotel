package com.protec.recervhotel.service;

import com.protec.recervhotel.dto.*;
import com.protec.recervhotel.entities.Usuario;
import com.protec.recervhotel.enums.Rol;
import com.protec.recervhotel.exception.BusinessException;
import com.protec.recervhotel.exception.ResourceNotFoundException;
import com.protec.recervhotel.mappers.UsuarioMapper;
import com.protec.recervhotel.persistencia.UsuarioDao;
import com.protec.recervhotel.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioDao usuarioDao;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private UsuarioRegistroDTO registroDTO;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .email("juan@test.com")
                .password("encodedPass")
                .telefono("123456789")
                .rol(Rol.USER)
                .build();

        usuarioDTO = UsuarioDTO.builder()
                .id(1L)
                .nombre("Juan")
                .email("juan@test.com")
                .telefono("123456789")
                .rol(Rol.USER)
                .build();

        registroDTO = UsuarioRegistroDTO.builder()
                .nombre("Juan")
                .email("juan@test.com")
                .password("password123")
                .telefono("123456789")
                .build();
    }

    @Test
    void registrar_WhenEmailNotTaken_CreatesUser() {
        when(usuarioDao.findByEmail(registroDTO.getEmail())).thenReturn(Optional.empty());
        when(usuarioMapper.toEntity(registroDTO)).thenReturn(usuario);
        when(passwordEncoder.encode(registroDTO.getPassword())).thenReturn("encodedPass");
        when(usuarioMapper.toDto(usuario)).thenReturn(usuarioDTO);

        UsuarioDTO result = usuarioService.registrar(registroDTO);

        assertNotNull(result);
        assertEquals("Juan", result.getNombre());
        assertEquals(Rol.USER, result.getRol());
        verify(usuarioDao).save(usuario);
    }

    @Test
    void registrar_WhenEmailTaken_ThrowsException() {
        when(usuarioDao.findByEmail(registroDTO.getEmail())).thenReturn(Optional.of(usuario));

        assertThrows(BusinessException.class, () -> usuarioService.registrar(registroDTO));
        verify(usuarioDao, never()).save(any());
    }

    @Test
    void login_WithValidCredentials_ReturnsToken() {
        UsuarioLoginDTO loginDTO = UsuarioLoginDTO.builder()
                .email("juan@test.com")
                .password("password123")
                .build();

        when(usuarioDao.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(loginDTO.getPassword(), usuario.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(usuario.getEmail(), "USER")).thenReturn("token123");

        LoginResponseDTO result = usuarioService.login(loginDTO);

        assertNotNull(result);
        assertEquals("token123", result.getToken());
        assertEquals("Juan", result.getNombre());
        assertEquals(Rol.USER, result.getRol());
    }

    @Test
    void login_WithWrongPassword_ThrowsException() {
        UsuarioLoginDTO loginDTO = UsuarioLoginDTO.builder()
                .email("juan@test.com")
                .password("wrongpass")
                .build();

        when(usuarioDao.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(loginDTO.getPassword(), usuario.getPassword())).thenReturn(false);

        assertThrows(BusinessException.class, () -> usuarioService.login(loginDTO));
    }

    @Test
    void login_WithUnknownEmail_ThrowsException() {
        UsuarioLoginDTO loginDTO = UsuarioLoginDTO.builder()
                .email("unknown@test.com")
                .password("pass")
                .build();

        when(usuarioDao.findByEmail(loginDTO.getEmail())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> usuarioService.login(loginDTO));
    }

    @Test
    void obtenerPorId_WhenExists_ReturnsUsuario() {
        when(usuarioDao.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toDto(usuario)).thenReturn(usuarioDTO);

        UsuarioDTO result = usuarioService.obtenerPorId(1L);

        assertEquals("Juan", result.getNombre());
    }

    @Test
    void obtenerPorId_WhenNotExists_ThrowsException() {
        when(usuarioDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.obtenerPorId(99L));
    }

    @Test
    void listarTodos_ReturnsAll() {
        when(usuarioDao.findAll()).thenReturn(List.of(usuario));
        when(usuarioMapper.toListDto(List.of(usuario))).thenReturn(List.of(usuarioDTO));

        List<UsuarioDTO> results = usuarioService.listarTodos();

        assertEquals(1, results.size());
    }

    @Test
    void eliminar_WhenExists_Deletes() {
        when(usuarioDao.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.eliminar(1L);

        verify(usuarioDao).delete(1L);
    }

    @Test
    void eliminar_WhenNotExists_ThrowsException() {
        when(usuarioDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.eliminar(99L));
    }

    @Test
    void crearPorAdmin_CreatesWithSpecifiedRol() {
        UsuarioRegistroDTO adminDto = UsuarioRegistroDTO.builder()
                .nombre("Admin")
                .email("admin@test.com")
                .password("pass")
                .rol(Rol.ADMIN)
                .build();

        Usuario adminUser = Usuario.builder().id(2L).nombre("Admin").email("admin@test.com")
                .password("encoded").rol(Rol.ADMIN).build();

        UsuarioDTO adminDtoResp = UsuarioDTO.builder().id(2L).nombre("Admin")
                .email("admin@test.com").rol(Rol.ADMIN).build();

        when(usuarioDao.findByEmail(adminDto.getEmail())).thenReturn(Optional.empty());
        when(usuarioMapper.toEntity(adminDto)).thenReturn(adminUser);
        when(passwordEncoder.encode(adminDto.getPassword())).thenReturn("encoded");
        when(usuarioMapper.toDto(adminUser)).thenReturn(adminDtoResp);

        UsuarioDTO result = usuarioService.crearPorAdmin(adminDto);

        assertEquals(Rol.ADMIN, result.getRol());
        verify(usuarioDao).save(adminUser);
    }

    @Test
    void actualizar_WithNewEmail_UpdatesSuccessfully() {
        UsuarioActualizacionDTO dto = UsuarioActualizacionDTO.builder()
                .nombre("Juan Updated")
                .email("nuevo@test.com")
                .telefono("987654321")
                .build();

        when(usuarioDao.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioDao.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        UsuarioDTO updatedDto = UsuarioDTO.builder().id(1L).nombre("Juan Updated")
                .email("nuevo@test.com").telefono("987654321").rol(Rol.USER).build();
        when(usuarioMapper.toDto(usuario)).thenReturn(updatedDto);

        UsuarioDTO result = usuarioService.actualizar(1L, dto);

        assertEquals("Juan Updated", result.getNombre());
        assertEquals("nuevo@test.com", result.getEmail());
        verify(usuarioDao).save(usuario);
    }

    @Test
    void cambiarRol_UpdatesRole() {
        when(usuarioDao.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioDTO adminDto = UsuarioDTO.builder().id(1L).nombre("Juan")
                .email("juan@test.com").rol(Rol.ADMIN).build();
        when(usuarioMapper.toDto(usuario)).thenReturn(adminDto);

        UsuarioDTO result = usuarioService.cambiarRol(1L, Rol.ADMIN);

        assertEquals(Rol.ADMIN, result.getRol());
        verify(usuarioDao).save(usuario);
    }
}
