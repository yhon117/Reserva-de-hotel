package com.protec.recervhotel.service;

import com.protec.recervhotel.dto.HabitacionCreacionDTO;
import com.protec.recervhotel.dto.HabitacionDTO;
import com.protec.recervhotel.entities.Habitacion;
import com.protec.recervhotel.enums.EstadoHab;
import com.protec.recervhotel.enums.TipoHab;
import com.protec.recervhotel.exception.BusinessException;
import com.protec.recervhotel.exception.ResourceNotFoundException;
import com.protec.recervhotel.mappers.HabitacionMapper;
import com.protec.recervhotel.persistencia.HabitacionDao;
import com.protec.recervhotel.persistencia.ReservaDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitacionServiceTest {

    @Mock
    private HabitacionDao habitacionDao;

    @Mock
    private HabitacionMapper habitacionMapper;

    @Mock
    private ReservaDao reservaDao;

    @InjectMocks
    private HabitacionService habitacionService;

    private Habitacion habitacion;
    private HabitacionDTO habitacionDTO;
    private HabitacionCreacionDTO creacionDTO;

    @BeforeEach
    void setUp() {
        habitacion = Habitacion.builder()
                .id(1L)
                .numero("101")
                .piso(1)
                .precioNoche(100.0)
                .capacidad(2)
                .tipo(TipoHab.SIMPLE)
                .estado(EstadoHab.DISPONIBLE)
                .build();

        habitacionDTO = HabitacionDTO.builder()
                .id(1L)
                .numero("101")
                .piso(1)
                .precioNoche(100.0)
                .capacidad(2)
                .tipo("SIMPLE")
                .estado("DISPONIBLE")
                .build();

        creacionDTO = HabitacionCreacionDTO.builder()
                .numero("101")
                .piso(1)
                .precioNoche(100.0)
                .capacidad(2)
                .tipo("SIMPLE")
                .estado("DISPONIBLE")
                .build();

        lenient().when(reservaDao.findHabitacionesOcupadasEnFecha(any(LocalDate.class)))
                .thenReturn(List.of());
    }

    @Test
    void crear_WhenNumeroNotTaken_CreatesHabitacion() {
        when(habitacionDao.findByNumero("101")).thenReturn(null);
        when(habitacionMapper.toEntity(creacionDTO)).thenReturn(habitacion);
        when(habitacionMapper.toDto(habitacion)).thenReturn(habitacionDTO);

        HabitacionDTO result = habitacionService.crear(creacionDTO);

        assertNotNull(result);
        assertEquals("101", result.getNumero());
        verify(habitacionDao).save(habitacion);
    }

    @Test
    void crear_WhenNumeroTaken_ThrowsException() {
        when(habitacionDao.findByNumero("101")).thenReturn(habitacion);

        assertThrows(BusinessException.class, () -> habitacionService.crear(creacionDTO));
        verify(habitacionDao, never()).save(any());
    }

    @Test
    void obtenerPorId_WhenExists_ReturnsHabitacion() {
        when(habitacionDao.findById(1L)).thenReturn(Optional.of(habitacion));
        when(habitacionMapper.toDto(habitacion)).thenReturn(habitacionDTO);

        HabitacionDTO result = habitacionService.obtenerPorId(1L);

        assertEquals("101", result.getNumero());
        assertEquals("DISPONIBLE", result.getEstado());
    }

    @Test
    void obtenerPorId_WhenHasActiveReservation_ShowsOcupada() {
        var ocupaBuilder = Habitacion.builder()
                .id(1L).numero("101").piso(1).precioNoche(100.0).capacidad(2)
                .tipo(TipoHab.SIMPLE).estado(EstadoHab.DISPONIBLE);
        habitacion = ocupaBuilder.build();
        habitacionDTO = HabitacionDTO.builder()
                .id(1L).numero("101").piso(1).precioNoche(100.0).capacidad(2)
                .tipo("SIMPLE").estado("DISPONIBLE").build();
        when(reservaDao.findHabitacionesOcupadasEnFecha(any(LocalDate.class)))
                .thenReturn(List.of(1L));
        when(habitacionDao.findById(1L)).thenReturn(Optional.of(habitacion));
        when(habitacionMapper.toDto(habitacion)).thenReturn(habitacionDTO);

        HabitacionDTO result = habitacionService.obtenerPorId(1L);

        assertEquals("OCUPADA", result.getEstado());
    }

    @Test
    void obtenerPorId_WhenMantenimiento_ShowsMantenimiento() {
        var mantBuilder = Habitacion.builder()
                .id(1L).numero("101").piso(1).precioNoche(100.0).capacidad(2)
                .tipo(TipoHab.SIMPLE).estado(EstadoHab.MANTENIMIENTO);
        habitacion = mantBuilder.build();
        habitacionDTO = HabitacionDTO.builder()
                .id(1L).numero("101").piso(1).precioNoche(100.0).capacidad(2)
                .tipo("SIMPLE").estado("MANTENIMIENTO").build();
        when(habitacionDao.findById(1L)).thenReturn(Optional.of(habitacion));
        when(habitacionMapper.toDto(habitacion)).thenReturn(habitacionDTO);

        HabitacionDTO result = habitacionService.obtenerPorId(1L);

        assertEquals("MANTENIMIENTO", result.getEstado());
    }

    @Test
    void obtenerPorId_WhenNotExists_ThrowsException() {
        when(habitacionDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> habitacionService.obtenerPorId(99L));
    }

    @Test
    void listarTodas_ReturnsAll() {
        when(habitacionDao.findAll()).thenReturn(List.of(habitacion));
        when(habitacionMapper.toListDto(List.of(habitacion))).thenReturn(List.of(habitacionDTO));

        List<HabitacionDTO> results = habitacionService.listarTodas();

        assertEquals(1, results.size());
        assertEquals("DISPONIBLE", results.getFirst().getEstado());
    }

    @Test
    void listarTodas_WithSomeOccupied_ShowsEffectiveEstado() {
        var hab1 = Habitacion.builder().id(1L).numero("101").piso(1).precioNoche(100.0).capacidad(2)
                .tipo(TipoHab.SIMPLE).estado(EstadoHab.DISPONIBLE).build();
        var hab2 = Habitacion.builder().id(2L).numero("202").piso(2).precioNoche(150.0).capacidad(3)
                .tipo(TipoHab.DOBLE).estado(EstadoHab.DISPONIBLE).build();
        var hab3 = Habitacion.builder().id(3L).numero("303").piso(3).precioNoche(200.0).capacidad(4)
                .tipo(TipoHab.SUITE).estado(EstadoHab.MANTENIMIENTO).build();

        var dto1 = HabitacionDTO.builder().id(1L).numero("101").piso(1).precioNoche(100.0).capacidad(2)
                .tipo("SIMPLE").estado("DISPONIBLE").build();
        var dto2 = HabitacionDTO.builder().id(2L).numero("202").piso(2).precioNoche(150.0).capacidad(3)
                .tipo("DOBLE").estado("DISPONIBLE").build();
        var dto3 = HabitacionDTO.builder().id(3L).numero("303").piso(3).precioNoche(200.0).capacidad(4)
                .tipo("SUITE").estado("MANTENIMIENTO").build();

        when(reservaDao.findHabitacionesOcupadasEnFecha(any(LocalDate.class)))
                .thenReturn(List.of(1L)); // only hab1 has active reservation
        when(habitacionDao.findAll()).thenReturn(List.of(hab1, hab2, hab3));
        when(habitacionMapper.toListDto(List.of(hab1, hab2, hab3))).thenReturn(List.of(dto1, dto2, dto3));

        var results = habitacionService.listarTodas();

        assertEquals(3, results.size());
        assertEquals("OCUPADA", results.get(0).getEstado());
        assertEquals("DISPONIBLE", results.get(1).getEstado());
        assertEquals("MANTENIMIENTO", results.get(2).getEstado());
    }

    @Test
    void actualizar_WhenValid_Updates() {
        when(habitacionDao.findById(1L)).thenReturn(Optional.of(habitacion));
        when(habitacionDao.findByNumero("101")).thenReturn(habitacion);

        HabitacionDTO updatedDto = HabitacionDTO.builder().id(1L).numero("101")
                .piso(2).precioNoche(150.0).capacidad(3)
                .tipo("SIMPLE").estado("DISPONIBLE").build();
        when(habitacionMapper.toDto(habitacion)).thenReturn(updatedDto);

        HabitacionDTO result = habitacionService.actualizar(1L, creacionDTO);

        assertEquals(2, result.getPiso());
        assertEquals(150.0, result.getPrecioNoche());
        verify(habitacionDao).save(habitacion);
    }

    @Test
    void actualizar_WhenNotExists_ThrowsException() {
        when(habitacionDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> habitacionService.actualizar(99L, creacionDTO));
    }

    @Test
    void eliminar_WhenExists_Deletes() {
        when(habitacionDao.findById(1L)).thenReturn(Optional.of(habitacion));

        habitacionService.eliminar(1L);

        verify(habitacionDao).delete(1L);
    }

    @Test
    void eliminar_WhenNotExists_ThrowsException() {
        when(habitacionDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> habitacionService.eliminar(99L));
    }

    @Test
    void buscarDisponiblesPorTipo_ReturnsFiltered() {
        when(habitacionDao.findDisponiblesByTipo(TipoHab.SIMPLE)).thenReturn(List.of(habitacion));
        when(habitacionMapper.toListDto(List.of(habitacion))).thenReturn(List.of(habitacionDTO));

        List<HabitacionDTO> results = habitacionService.buscarDisponiblesPorTipo("SIMPLE");

        assertEquals(1, results.size());
    }

    @Test
    void buscarDisponiblesPorTipo_FiltersOutOccupied() {
        var hab1 = Habitacion.builder().id(1L).numero("101").piso(1).precioNoche(100.0).capacidad(2)
                .tipo(TipoHab.SIMPLE).estado(EstadoHab.DISPONIBLE).build();
        var hab2 = Habitacion.builder().id(2L).numero("102").piso(1).precioNoche(100.0).capacidad(2)
                .tipo(TipoHab.SIMPLE).estado(EstadoHab.DISPONIBLE).build();

        var dto1 = HabitacionDTO.builder().id(1L).numero("101").piso(1).precioNoche(100.0).capacidad(2)
                .tipo("SIMPLE").estado("DISPONIBLE").build();
        var dto2 = HabitacionDTO.builder().id(2L).numero("102").piso(1).precioNoche(100.0).capacidad(2)
                .tipo("SIMPLE").estado("DISPONIBLE").build();

        when(reservaDao.findHabitacionesOcupadasEnFecha(any(LocalDate.class)))
                .thenReturn(List.of(1L));
        when(habitacionDao.findDisponiblesByTipo(TipoHab.SIMPLE)).thenReturn(List.of(hab1, hab2));
        when(habitacionMapper.toListDto(List.of(hab1, hab2))).thenReturn(List.of(dto1, dto2));

        var results = habitacionService.buscarDisponiblesPorTipo("SIMPLE");

        assertEquals(1, results.size());
        assertEquals(2L, results.getFirst().getId());
    }

    @Test
    void buscarDisponiblesPorTipo_WithInvalidTipo_ThrowsException() {
        assertThrows(BusinessException.class,
                () -> habitacionService.buscarDisponiblesPorTipo("INVALIDO"));
    }

    @Test
    void buscarDisponiblesPorPrecioMaximo_ReturnsFiltered() {
        when(habitacionDao.findDisponiblesByPrecioMaximo(150.0)).thenReturn(List.of(habitacion));
        when(habitacionMapper.toListDto(List.of(habitacion))).thenReturn(List.of(habitacionDTO));

        List<HabitacionDTO> results = habitacionService.buscarDisponiblesPorPrecioMaximo(150.0);

        assertEquals(1, results.size());
    }

    @Test
    void buscarDisponiblesPorCapacidad_ReturnsFiltered() {
        when(habitacionDao.findDisponiblesByCapacidad(2)).thenReturn(List.of(habitacion));
        when(habitacionMapper.toListDto(List.of(habitacion))).thenReturn(List.of(habitacionDTO));

        List<HabitacionDTO> results = habitacionService.buscarDisponiblesPorCapacidad(2);

        assertEquals(1, results.size());
    }
}
