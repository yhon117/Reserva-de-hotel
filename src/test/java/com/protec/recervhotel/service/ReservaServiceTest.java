package com.protec.recervhotel.service;

import com.protec.recervhotel.dto.ReservaCreacionDTO;
import com.protec.recervhotel.dto.ReservaDTO;
import com.protec.recervhotel.entities.Habitacion;
import com.protec.recervhotel.entities.Reserva;
import com.protec.recervhotel.entities.Usuario;
import com.protec.recervhotel.enums.Estado;
import com.protec.recervhotel.enums.EstadoHab;
import com.protec.recervhotel.enums.Rol;
import com.protec.recervhotel.enums.TipoHab;
import com.protec.recervhotel.exception.BusinessException;
import com.protec.recervhotel.exception.ResourceNotFoundException;
import com.protec.recervhotel.mappers.ReservaMapper;
import com.protec.recervhotel.persistencia.HabitacionDao;
import com.protec.recervhotel.persistencia.ReservaDao;
import com.protec.recervhotel.persistencia.UsuarioDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaDao reservaDao;
    @Mock
    private UsuarioDao usuarioDao;
    @Mock
    private HabitacionDao habitacionDao;
    @Mock
    private ReservaMapper reservaMapper;

    @InjectMocks
    private ReservaService reservaService;

    private Usuario usuario;
    private Habitacion habitacion;
    private Reserva reserva;
    private ReservaDTO reservaDTO;
    private ReservaCreacionDTO creacionDTO;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder().id(1L).nombre("Juan").email("juan@test.com")
                .password("pass").rol(Rol.USER).build();

        habitacion = Habitacion.builder().id(1L).numero("101").piso(1)
                .precioNoche(100.0).capacidad(2).tipo(TipoHab.SIMPLE)
                .estado(EstadoHab.DISPONIBLE).build();

        reserva = Reserva.builder().id(1L)
                .fechaEntrada(LocalDate.now().plusDays(1))
                .fechaSalida(LocalDate.now().plusDays(3))
                .total(200.0).estado(Estado.CONFIRMADA)
                .usuario(usuario).habitacion(habitacion)
                .build();

        reservaDTO = ReservaDTO.builder().id(1L)
                .fechaEntrada(LocalDate.now().plusDays(1))
                .fechaSalida(LocalDate.now().plusDays(3))
                .total(200.0).estado("CONFIRMADA")
                .usuarioId(1L).habitacionId(1L)
                .usuarioNombre("Juan").habitacionNumero("101")
                .build();

        creacionDTO = ReservaCreacionDTO.builder()
                .fechaEntrada(LocalDate.now().plusDays(1))
                .fechaSalida(LocalDate.now().plusDays(3))
                .usuarioId(1L).habitacionId(1L)
                .build();
    }

    @Test
    void crear_WithValidData_CreatesReserva() {
        when(usuarioDao.findById(1L)).thenReturn(Optional.of(usuario));
        when(habitacionDao.findById(1L)).thenReturn(Optional.of(habitacion));
        when(reservaDao.findByHabitacionEnRango(1L, creacionDTO.getFechaEntrada(),
                creacionDTO.getFechaSalida())).thenReturn(List.of());
        when(reservaMapper.toEntity(creacionDTO)).thenReturn(reserva);
        when(reservaMapper.toDto(reserva)).thenReturn(reservaDTO);

        ReservaDTO result = reservaService.crear(creacionDTO);

        assertNotNull(result);
        assertEquals(200.0, result.getTotal());
        verify(reservaDao).save(reserva);
    }

    @Test
    void crear_WhenUsuarioNotExists_ThrowsException() {
        when(usuarioDao.findById(99L)).thenReturn(Optional.empty());
        ReservaCreacionDTO dto = ReservaCreacionDTO.builder()
                .fechaEntrada(LocalDate.now().plusDays(1))
                .fechaSalida(LocalDate.now().plusDays(3))
                .usuarioId(99L).habitacionId(1L).build();

        assertThrows(ResourceNotFoundException.class, () -> reservaService.crear(dto));
    }

    @Test
    void crear_WhenHabitacionNotExists_ThrowsException() {
        when(usuarioDao.findById(1L)).thenReturn(Optional.of(usuario));
        when(habitacionDao.findById(99L)).thenReturn(Optional.empty());
        ReservaCreacionDTO dto = ReservaCreacionDTO.builder()
                .fechaEntrada(LocalDate.now().plusDays(1))
                .fechaSalida(LocalDate.now().plusDays(3))
                .usuarioId(1L).habitacionId(99L).build();

        assertThrows(ResourceNotFoundException.class, () -> reservaService.crear(dto));
    }

    @Test
    void crear_WhenHabitacionNotDisponible_ThrowsException() {
        habitacion.setEstado(EstadoHab.OCUPADA);
        when(usuarioDao.findById(1L)).thenReturn(Optional.of(usuario));
        when(habitacionDao.findById(1L)).thenReturn(Optional.of(habitacion));

        assertThrows(BusinessException.class, () -> reservaService.crear(creacionDTO));
    }

    @Test
    void crear_WhenFechaIngresoAntesDeHoy_ThrowsException() {
        ReservaCreacionDTO dto = ReservaCreacionDTO.builder()
                .fechaEntrada(LocalDate.now().minusDays(1))
                .fechaSalida(LocalDate.now().plusDays(2))
                .usuarioId(1L).habitacionId(1L).build();

        when(usuarioDao.findById(1L)).thenReturn(Optional.of(usuario));
        when(habitacionDao.findById(1L)).thenReturn(Optional.of(habitacion));

        assertThrows(BusinessException.class, () -> reservaService.crear(dto));
    }

    @Test
    void crear_WhenFechaSalidaAntesDeEntrada_ThrowsException() {
        ReservaCreacionDTO dto = ReservaCreacionDTO.builder()
                .fechaEntrada(LocalDate.now().plusDays(5))
                .fechaSalida(LocalDate.now().plusDays(3))
                .usuarioId(1L).habitacionId(1L).build();

        when(usuarioDao.findById(1L)).thenReturn(Optional.of(usuario));
        when(habitacionDao.findById(1L)).thenReturn(Optional.of(habitacion));

        assertThrows(BusinessException.class, () -> reservaService.crear(dto));
    }

    @Test
    void crear_WhenSolapamiento_ThrowsException() {
        when(usuarioDao.findById(1L)).thenReturn(Optional.of(usuario));
        when(habitacionDao.findById(1L)).thenReturn(Optional.of(habitacion));
        when(reservaDao.findByHabitacionEnRango(1L, creacionDTO.getFechaEntrada(),
                creacionDTO.getFechaSalida())).thenReturn(List.of(reserva));

        assertThrows(BusinessException.class, () -> reservaService.crear(creacionDTO));
    }

    @Test
    void cancelar_WhenExists_CancelsReserva() {
        when(reservaDao.findById(1L)).thenReturn(Optional.of(reserva));
        ReservaDTO cancelledDto = ReservaDTO.builder().id(1L).estado("CANCELADA").build();
        when(reservaMapper.toDto(reserva)).thenReturn(cancelledDto);

        ReservaDTO result = reservaService.cancelar(1L);

        assertEquals("CANCELADA", result.getEstado());
        assertEquals(Estado.CANCELADA, reserva.getEstado());
        verify(reservaDao).save(reserva);
    }

    @Test
    void cancelar_WhenAlreadyCancelled_ThrowsException() {
        reserva.setEstado(Estado.CANCELADA);
        when(reservaDao.findById(1L)).thenReturn(Optional.of(reserva));

        assertThrows(BusinessException.class, () -> reservaService.cancelar(1L));
    }

    @Test
    void cancelar_WhenNotExists_ThrowsException() {
        when(reservaDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservaService.cancelar(99L));
    }

    @Test
    void obtenerPorId_WhenExists_ReturnsReserva() {
        when(reservaDao.findById(1L)).thenReturn(Optional.of(reserva));
        when(reservaMapper.toDto(reserva)).thenReturn(reservaDTO);

        ReservaDTO result = reservaService.obtenerPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void obtenerPorId_WhenNotExists_ThrowsException() {
        when(reservaDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservaService.obtenerPorId(99L));
    }

    @Test
    void listarTodas_ReturnsAll() {
        when(reservaDao.findAll()).thenReturn(List.of(reserva));
        when(reservaMapper.toListDto(List.of(reserva))).thenReturn(List.of(reservaDTO));

        List<ReservaDTO> results = reservaService.listarTodas();

        assertEquals(1, results.size());
    }

    @Test
    void listarPorHabitacion_WhenExists_ReturnsReservas() {
        when(habitacionDao.findById(1L)).thenReturn(Optional.of(habitacion));
        when(reservaDao.findByHabitacionId(1L)).thenReturn(List.of(reserva));
        when(reservaMapper.toListDto(List.of(reserva))).thenReturn(List.of(reservaDTO));

        List<ReservaDTO> results = reservaService.listarPorHabitacion(1L);

        assertEquals(1, results.size());
    }

    @Test
    void listarPorHabitacion_WhenNotExists_ThrowsException() {
        when(habitacionDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reservaService.listarPorHabitacion(99L));
    }

    @Test
    void contarOcupadasEnFecha_DelegatesToDao() {
        when(reservaDao.contarOcupadasEnFecha(LocalDate.now())).thenReturn(3L);

        Long count = reservaService.contarOcupadasEnFecha(LocalDate.now());

        assertEquals(3L, count);
    }

    @Test
    void ingresosPorMes_ReturnsMappedData() {
        when(reservaDao.ingresosPorMes()).thenReturn(
                List.<Object[]>of(new Object[]{6, 2026, 5000.0}));

        List<Map<String, Object>> results = reservaService.ingresosPorMes();

        assertEquals(1, results.size());
        assertEquals(6, results.get(0).get("mes"));
        assertEquals(2026, results.get(0).get("anio"));
        assertEquals(5000.0, results.get(0).get("total"));
    }

    @Test
    void ocupacionAgrupadaPorMes_ReturnsMappedData() {
        when(reservaDao.ocupacionAgrupadaPorMes()).thenReturn(
                List.<Object[]>of(new Object[]{6, 2026, 15L}));

        List<Map<String, Object>> results = reservaService.ocupacionAgrupadaPorMes();

        assertEquals(1, results.size());
        assertEquals(15L, results.get(0).get("cantidad"));
    }

    @Test
    void tendenciaDesde_ReturnsMappedData() {
        when(reservaDao.tendenciaDesde(LocalDate.now().minusDays(7)))
                .thenReturn(List.<Object[]>of(new Object[]{LocalDate.now(), 5L}));

        List<Map<String, Object>> results = reservaService.tendenciaDesde(LocalDate.now().minusDays(7));

        assertEquals(1, results.size());
        assertEquals(LocalDate.now(), results.get(0).get("fecha"));
        assertEquals(5L, results.get(0).get("cantidad"));
    }

    @Test
    void habitacionesMasReservadas_ReturnsMappedData() {
        when(reservaDao.habitacionesMasReservadas())
                .thenReturn(List.<Object[]>of(new Object[]{1L, "101", 10L}));

        List<Map<String, Object>> results = reservaService.habitacionesMasReservadas();

        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).get("habitacionId"));
        assertEquals("101", results.get(0).get("numero"));
        assertEquals(10L, results.get(0).get("total"));
    }
}
