package com.protec.recervhotel.repository;

import com.protec.recervhotel.entitys.Reserva;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaReposistory extends CrudRepository<Reserva,Long> {

    // ─────────────────────────────────────────────
    // % OCUPACIÓN DIARIA
    // ─────────────────────────────────────────────

    // Cuántas habitaciones están ocupadas en un día específico
    @Query("""
            SELECT COUNT(r) FROM Reserva r
            WHERE r.estado = 'CONFIRMADA'
            AND :fecha BETWEEN r.fechaEntrada AND r.fechaSalida
            """)
    Long contarOcupadasEnFecha(@Param("fecha") LocalDate fecha);

    // % de ocupación en un día: (ocupadas / total_habitaciones) * 100
    // Se usa junto a habitacionRepository.count() en el servicio
    @Query("""
            SELECT COUNT(DISTINCT r.habitacion.id) FROM Reserva r
            WHERE r.estado = 'CONFIRMADA'
            AND :fecha BETWEEN r.fechaEntrada AND r.fechaSalida
            """)
    Long contarHabitacionesOcupadasEnFecha(@Param("fecha") LocalDate fecha);

    // ─────────────────────────────────────────────
    // % OCUPACIÓN SEMANAL
    // ─────────────────────────────────────────────

    // Reservas activas dentro de un rango de fechas (semana o mes)
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

    // Ocupación agrupada por día dentro de un rango (para gráficas de tendencia)
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

    // ─────────────────────────────────────────────
    // % OCUPACIÓN MENSUAL
    // ─────────────────────────────────────────────

    // Reservas agrupadas por mes y año (para tendencia mensual)
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

    // Ingresos totales por mes
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

    // ─────────────────────────────────────────────
    // TENDENCIA DE OCUPACIÓN
    // ─────────────────────────────────────────────

    // Tendencia: reservas de los últimos N días (para ver si sube o baja)
    @Query("""
            SELECT r.fechaEntrada, COUNT(r) FROM Reserva r
            WHERE r.fechaEntrada >= :desde
            GROUP BY r.fechaEntrada
            ORDER BY r.fechaEntrada ASC
            """)
    List<Object[]> tendenciaDesde(@Param("desde") LocalDate desde);

    // Habitación más reservada
    @Query("""
            SELECT r.habitacion.id, r.habitacion.numero, COUNT(r)
            FROM Reserva r
            WHERE r.estado = 'CONFIRMADA'
            GROUP BY r.habitacion.id, r.habitacion.numero
            ORDER BY COUNT(r) DESC
            """)
    List<Object[]> habitacionesMasReservadas();

    // ─────────────────────────────────────────────
    // HISTORIAL POR HABITACIÓN ESPECÍFICA
    // ─────────────────────────────────────────────

    // Todas las reservas de una habitación específica
    @Query("SELECT r FROM Reserva r WHERE r.habitacion.id = :habitacionId ORDER BY r.fechaEntrada DESC")
    List<Reserva> findByHabitacionId(@Param("habitacionId") Long habitacionId);

    // Reservas de una habitación en un rango de fechas
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
