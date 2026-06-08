package com.protec.recervhotel.repository;

import com.protec.recervhotel.entities.Habitacion;
import com.protec.recervhotel.enums.EstadoHab;
import com.protec.recervhotel.enums.TipoHab;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class HabitacionRepositoryTest {

    @Autowired
    private HabitacionRepository habitacionRepository;

    private Habitacion habSimple;
    private Habitacion habDoble;

    @BeforeEach
    void setUp() {
        habSimple = Habitacion.builder()
                .numero("101")
                .piso(1)
                .precioNoche(100.0)
                .capacidad(2)
                .tipo(TipoHab.SIMPLE)
                .estado(EstadoHab.DISPONIBLE)
                .build();

        habDoble = Habitacion.builder()
                .numero("202")
                .piso(2)
                .precioNoche(200.0)
                .capacidad(4)
                .tipo(TipoHab.DOBLE)
                .estado(EstadoHab.DISPONIBLE)
                .build();

        habitacionRepository.save(habSimple);
        habitacionRepository.save(habDoble);
    }

    @Test
    void findByNumero_WhenExists_ReturnsHabitacion() {
        Habitacion result = habitacionRepository.findByNumero("101");

        assertNotNull(result);
        assertEquals(1, result.getPiso());
    }

    @Test
    void findByNumero_WhenNotExists_ReturnsNull() {
        Habitacion result = habitacionRepository.findByNumero("999");

        assertNull(result);
    }

    @Test
    void findDisponiblesByTipo_ReturnsMatching() {
        List<Habitacion> results = habitacionRepository.findDisponiblesByTipo(TipoHab.SIMPLE);

        assertEquals(1, results.size());
        assertEquals("101", results.get(0).getNumero());
    }

    @Test
    void findDisponiblesByPrecioMaximo_ReturnsFiltered() {
        List<Habitacion> results = habitacionRepository.findDisponiblesByPrecioMaximo(150.0);

        assertEquals(1, results.size());
        assertEquals("101", results.get(0).getNumero());
    }

    @Test
    void findDisponiblesByCapacidad_ReturnsFiltered() {
        List<Habitacion> results = habitacionRepository.findDisponiblesByCapacidad(3);

        assertEquals(1, results.size());
        assertEquals("202", results.get(0).getNumero());
    }

    @Test
    void findByEstado_ReturnsMatching() {
        List<Habitacion> results = habitacionRepository.findByEstado(EstadoHab.DISPONIBLE);

        assertEquals(2, results.size());
    }

    @Test
    void findByEstado_WhenMantenimiento_ReturnsEmpty() {
        habitacionRepository.save(Habitacion.builder()
                .numero("303").piso(3).precioNoche(150.0).capacidad(2)
                .tipo(TipoHab.SIMPLE).estado(EstadoHab.MANTENIMIENTO).build());

        List<Habitacion> disponibles = habitacionRepository.findByEstado(EstadoHab.DISPONIBLE);
        List<Habitacion> mantenimiento = habitacionRepository.findByEstado(EstadoHab.MANTENIMIENTO);

        assertEquals(2, disponibles.size());
        assertEquals(1, mantenimiento.size());
    }

    @Test
    void contarPorEstado_ReturnsCounts() {
        List<Object[]> counts = habitacionRepository.contarPorEstado();

        assertEquals(1, counts.size());
        assertEquals(EstadoHab.DISPONIBLE, counts.getFirst()[0]);
        assertEquals(2L, counts.getFirst()[1]);
    }

    @Test
    void findByPiso_ReturnsFiltered() {
        List<Habitacion> results = habitacionRepository.findByPiso(1);

        assertEquals(1, results.size());
    }
}
