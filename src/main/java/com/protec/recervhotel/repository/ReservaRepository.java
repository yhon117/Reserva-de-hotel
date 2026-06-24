package com.protec.recervhotel.repository;

import com.protec.recervhotel.entities.Reserva;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends CrudRepository<Reserva,Long> {

    @Query("""
            SELECT COUNT(r) FROM Reserva r
            WHERE r.estado = 'CONFIRMADA'
            AND :fecha BETWEEN r.fechaEntrada AND r.fechaSalida
            """)
    Long contarOcupadasEnFecha(@Param("fecha") LocalDate fecha);

    @Query("""
            SELECT COUNT(DISTINCT r.habitacion.id) FROM Reserva r
            WHERE r.estado = 'CONFIRMADA'
            AND :fecha BETWEEN r.fechaEntrada AND r.fechaSalida
            """)
    Long contarHabitacionesOcupadasEnFecha(@Param("fecha") LocalDate fecha);

    @Query("""
            SELECT r FROM Reserva r
            WHERE r.estado = 'CONFIRMADA'
            AND r.fechaEntrada <= :fin
            AND r.fechaSalida >= :inicio
            """)
    List<Reserva> findReservasEnRango(
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );

    @Query("""
            SELECT r.fechaEntrada, COUNT(r) FROM Reserva r
            WHERE r.estado = 'CONFIRMADA'
            AND r.fechaEntrada BETWEEN :inicio AND :fin
            GROUP BY r.fechaEntrada
            ORDER BY r.fechaEntrada ASC
            """)
    List<Object[]> ocupacionPorDiaEnRango(
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );

    @Query("""
            SELECT FUNCTION('MONTH', r.fechaEntrada),
                   FUNCTION('YEAR', r.fechaEntrada),
                   COUNT(r)
            FROM Reserva r
            WHERE r.estado = 'CONFIRMADA'
            GROUP BY FUNCTION('YEAR', r.fechaEntrada), FUNCTION('MONTH', r.fechaEntrada)
            ORDER BY FUNCTION('YEAR', r.fechaEntrada), FUNCTION('MONTH', r.fechaEntrada)
            """)
    List<Object[]> ocupacionAgrupadaPorMes();

    @Query("""
            SELECT FUNCTION('MONTH', r.fechaEntrada),
                   FUNCTION('YEAR', r.fechaEntrada),
                   SUM(r.total)
            FROM Reserva r
            WHERE r.estado = 'CONFIRMADA'
            GROUP BY FUNCTION('YEAR', r.fechaEntrada), FUNCTION('MONTH', r.fechaEntrada)
            ORDER BY FUNCTION('YEAR', r.fechaEntrada), FUNCTION('MONTH', r.fechaEntrada)
            """)
    List<Object[]> ingresosPorMes();

    @Query("""
            SELECT r.fechaEntrada, COUNT(r) FROM Reserva r
            WHERE r.fechaEntrada >= :desde
            GROUP BY r.fechaEntrada
            ORDER BY r.fechaEntrada ASC
            """)
    List<Object[]> tendenciaDesde(@Param("desde") LocalDate desde);

    @Query("SELECT DISTINCT r.habitacion.id FROM Reserva r " +
           "WHERE r.estado IN ('CONFIRMADA', 'PENDIENTE') " +
           "AND :fecha BETWEEN r.fechaEntrada AND r.fechaSalida")
    List<Long> findHabitacionesOcupadasEnFecha(@Param("fecha") LocalDate fecha);

    @Query("""
            SELECT r.habitacion.id, r.habitacion.numero, COUNT(r)
            FROM Reserva r
            WHERE r.estado = 'CONFIRMADA'
            GROUP BY r.habitacion.id, r.habitacion.numero
            ORDER BY COUNT(r) DESC
            """)
    List<Object[]> habitacionesMasReservadas();

    List<Reserva> findByUsuarioIdOrderByFechaEntradaDesc(Long usuarioId);

    @Query("SELECT r FROM Reserva r WHERE r.estado = 'CONFIRMADA' AND r.fechaSalida < :hoy")
    List<Reserva> findVencidasSinCompletar(@Param("hoy") LocalDate hoy);

    @Query("SELECT r FROM Reserva r WHERE r.habitacion.id = :habitacionId ORDER BY r.fechaEntrada DESC")
    List<Reserva> findByHabitacionId(@Param("habitacionId") Long habitacionId);

    @Query("""
            SELECT r FROM Reserva r
            WHERE r.habitacion.id = :habitacionId
            AND r.fechaEntrada <= :fin
            AND r.fechaSalida >= :inicio
            """)
    List<Reserva> findByHabitacionEnRango(
            @Param("habitacionId") Long habitacionId,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin
    );

}
