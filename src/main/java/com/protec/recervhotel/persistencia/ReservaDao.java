package com.protec.recervhotel.persistencia;

import com.protec.recervhotel.entities.Habitacion;
import com.protec.recervhotel.entities.Reserva;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservaDao {
    void save(Reserva reserva);

    Optional<Reserva> findById(Long id);

    List<Reserva> findAll();

    void delete(Long id);

    Long contarOcupadasEnFecha(LocalDate fecha);

    Long contarHabitacionesOcupadasEnFecha(LocalDate fecha);

    List<Reserva> findReservasEnRango(LocalDate inicio, LocalDate fin);

    List<Object[]> ocupacionPorDiaEnRango(LocalDate inicio, LocalDate fin);

    List<Object[]> ocupacionAgrupadaPorMes();

    List<Object[]> ingresosPorMes();

    List<Object[]> tendenciaDesde( LocalDate desde);

    List<Reserva> findByUsuarioId(Long usuarioId);

    List<Reserva> findVencidasSinCompletar(LocalDate hoy);

    List<Reserva> findByHabitacionId(Long habitacionId);

    List<Reserva> findByHabitacionEnRango(Long habitacionId, LocalDate inicio, LocalDate fin);

    List<Object[]> habitacionesMasReservadas();

    List<Long> findHabitacionesOcupadasEnFecha(LocalDate fecha);
}
