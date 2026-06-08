package com.protec.recervhotel.repository;

import com.protec.recervhotel.entities.Habitacion;
import com.protec.recervhotel.entities.Reserva;
import com.protec.recervhotel.entities.Usuario;
import com.protec.recervhotel.enums.Estado;
import com.protec.recervhotel.enums.EstadoHab;
import com.protec.recervhotel.enums.Rol;
import com.protec.recervhotel.enums.TipoHab;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ReservaRepositoryTest {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private HabitacionRepository habitacionRepository;

    private Usuario usuario;
    private Habitacion habitacion;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        usuario = usuarioRepository.save(Usuario.builder()
                .nombre("Test User")
                .email("test@test.com")
                .password("pass")
                .rol(Rol.USER)
                .build());

        habitacion = habitacionRepository.save(Habitacion.builder()
                .numero("101")
                .piso(1)
                .precioNoche(100.0)
                .capacidad(2)
                .tipo(TipoHab.SIMPLE)
                .estado(EstadoHab.DISPONIBLE)
                .build());

        reserva = Reserva.builder()
                .fechaEntrada(LocalDate.now().plusDays(1))
                .fechaSalida(LocalDate.now().plusDays(3))
                .total(200.0)
                .estado(Estado.CONFIRMADA)
                .usuario(usuario)
                .habitacion(habitacion)
                .build();
        reserva.prePersist();
        reservaRepository.save(reserva);
    }

    @Test
    void contarOcupadasEnFecha_ReturnsCorrectCount() {
        Long count = reservaRepository.contarOcupadasEnFecha(LocalDate.now().plusDays(1));

        assertEquals(1L, count);
    }

    @Test
    void contarOcupadasEnFecha_WhenNoReservas_ReturnsZero() {
        Long count = reservaRepository.contarOcupadasEnFecha(LocalDate.now().plusMonths(6));

        assertEquals(0L, count);
    }

    @Test
    void findReservasEnRango_ReturnsOverlapping() {
        List<Reserva> results = reservaRepository.findReservasEnRango(
                LocalDate.now().plusDays(0), LocalDate.now().plusDays(5));

        assertEquals(1, results.size());
    }

    @Test
    void findReservasEnRango_WhenNoOverlap_ReturnsEmpty() {
        List<Reserva> results = reservaRepository.findReservasEnRango(
                LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(2));

        assertTrue(results.isEmpty());
    }

    @Test
    void findByHabitacionId_ReturnsReservas() {
        List<Reserva> results = reservaRepository.findByHabitacionId(habitacion.getId());

        assertFalse(results.isEmpty());
        assertEquals(habitacion.getId(), results.get(0).getHabitacion().getId());
    }

    @Test
    void findByHabitacionEnRango_ReturnsConflicting() {
        List<Reserva> results = reservaRepository.findByHabitacionEnRango(
                habitacion.getId(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3));

        assertFalse(results.isEmpty());
    }

    @Test
    void findByHabitacionEnRango_WhenNoConflict_ReturnsEmpty() {
        List<Reserva> results = reservaRepository.findByHabitacionEnRango(
                habitacion.getId(),
                LocalDate.now().plusMonths(3),
                LocalDate.now().plusMonths(4));

        assertTrue(results.isEmpty());
    }

    @Test
    void contarHabitacionesOcupadasEnFecha_ReturnsCount() {
        Long count = reservaRepository.contarHabitacionesOcupadasEnFecha(LocalDate.now().plusDays(2));

        assertEquals(1L, count);
    }

    @Test
    void ocupacionAgrupadaPorMes_ReturnsData() {
        List<Object[]> results = reservaRepository.ocupacionAgrupadaPorMes();

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    @Test
    void ingresosPorMes_ReturnsData() {
        List<Object[]> results = reservaRepository.ingresosPorMes();

        assertFalse(results.isEmpty());
        assertEquals(200.0, (Double) results.get(0)[2], 0.01);
    }

    @Test
    void tendenciaDesde_ReturnsData() {
        List<Object[]> results = reservaRepository.tendenciaDesde(LocalDate.now().minusDays(1));

        assertFalse(results.isEmpty());
        assertEquals(reserva.getFechaEntrada(), results.get(0)[0]);
    }

    @Test
    void habitacionesMasReservadas_ReturnsRanking() {
        List<Object[]> results = reservaRepository.habitacionesMasReservadas();

        assertFalse(results.isEmpty());
        assertEquals(habitacion.getId(), results.get(0)[0]);
        assertEquals(1L, results.get(0)[2]);
    }
}
