package com.protec.recervhotel;

import com.protec.recervhotel.dto.*;
import com.protec.recervhotel.enums.Rol;
import com.protec.recervhotel.repository.UsuarioRepository;
import com.protec.recervhotel.service.HabitacionService;
import com.protec.recervhotel.service.ReservaService;
import com.protec.recervhotel.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RecervHotelFullFlowIntegrationTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private HabitacionService habitacionService;

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long usuarioId;
    private Long adminId;
    private Long habitacionId;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
    }

    @Test
    void fullFlow_RegisterLoginCreateHabitacionCreateReservaCancel() {
        // 1. Register a user
        UsuarioRegistroDTO regDTO = UsuarioRegistroDTO.builder()
                .nombre("Juan Perez")
                .email("juan@test.com")
                .password("password123")
                .telefono("123456789")
                .build();

        UsuarioDTO user = usuarioService.registrar(regDTO);
        assertNotNull(user.getId());
        assertEquals("Juan Perez", user.getNombre());
        assertEquals(Rol.USER, user.getRol());
        usuarioId = user.getId();

        // 2. Login
        UsuarioLoginDTO loginDTO = UsuarioLoginDTO.builder()
                .email("juan@test.com")
                .password("password123")
                .build();
        LoginResponseDTO loginResp = usuarioService.login(loginDTO);
        assertNotNull(loginResp.getToken());
        assertEquals(Rol.USER, loginResp.getRol());

        // 3. Create habitacion (normally ADMIN does this)
        HabitacionCreacionDTO habDTO = HabitacionCreacionDTO.builder()
                .numero("101")
                .piso(1)
                .precioNoche(100.0)
                .capacidad(2)
                .tipo("SIMPLE")
                .estado("DISPONIBLE")
                .build();
        HabitacionDTO habitacion = habitacionService.crear(habDTO);
        assertNotNull(habitacion.getId());
        assertEquals("101", habitacion.getNumero());
        habitacionId = habitacion.getId();

        // 4. Find available rooms by type
        var disponibles = habitacionService.buscarDisponiblesPorTipo("SIMPLE");
        assertFalse(disponibles.isEmpty());

        // 5. Create reserva
        ReservaCreacionDTO resDTO = ReservaCreacionDTO.builder()
                .fechaEntrada(LocalDate.now().plusDays(1))
                .fechaSalida(LocalDate.now().plusDays(3))
                .usuarioId(usuarioId)
                .habitacionId(habitacionId)
                .build();
        ReservaDTO reserva = reservaService.crear(resDTO);
        assertNotNull(reserva.getId());
        assertEquals(200.0, reserva.getTotal());
        assertEquals("CONFIRMADA", reserva.getEstado());

        // 6. Check occupied count
        Long ocupadas = reservaService.contarOcupadasEnFecha(LocalDate.now().plusDays(2));
        assertEquals(1L, ocupadas);

        // 7. Verify stats before cancellation
        var ingresos = reservaService.ingresosPorMes();
        assertFalse(ingresos.isEmpty());

        var topHabitaciones = reservaService.habitacionesMasReservadas();
        assertFalse(topHabitaciones.isEmpty());

        // 8. Cancel reserva
        ReservaDTO cancelled = reservaService.cancelar(reserva.getId());
        assertEquals("CANCELADA", cancelled.getEstado());
    }

    @Test
    void registrar_DuplicateEmail_ThrowsException() {
        UsuarioRegistroDTO dto = UsuarioRegistroDTO.builder()
                .nombre("User")
                .email("duplicate@test.com")
                .password("pass")
                .build();
        usuarioService.registrar(dto);

        assertThrows(Exception.class, () -> usuarioService.registrar(dto));
    }

    @Test
    void passwordIsEncrypted() {
        UsuarioRegistroDTO dto = UsuarioRegistroDTO.builder()
                .nombre("Test")
                .email("encrypt@test.com")
                .password("plainPassword")
                .build();
        usuarioService.registrar(dto);

        var usuario = usuarioRepository.findByEmail("encrypt@test.com");
        assertTrue(usuario.isPresent());
        assertNotEquals("plainPassword", usuario.get().getPassword());
        assertTrue(passwordEncoder.matches("plainPassword", usuario.get().getPassword()));
    }
}
