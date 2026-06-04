package com.protec.recervhotel.repository;

import com.protec.recervhotel.enums.EstadoHab;
import com.protec.recervhotel.enums.TipoHab;
import com.protec.recervhotel.entities.Habitacion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitacionRepository extends CrudRepository<Habitacion,Long> {


    // ─────────────────────────────────────────────
    // HABITACIONES DISPONIBLES VS OCUPADAS
    // ─────────────────────────────────────────────

    // Cuenta habitaciones por estado (DISPONIBLE, OCUPADA, MANTENIMIENTO)
    @Query("SELECT h.estado, COUNT(h) FROM Habitacion h GROUP BY h.estado")
    List<Object[]> contarPorEstado();

    // Lista solo las disponibles
    List<Habitacion> findByEstado(EstadoHab estado);

    // ─────────────────────────────────────────────
    // TIPOS DE HABITACIÓN
    // ─────────────────────────────────────────────

    // Cuántas habitaciones hay de cada tipo
    @Query("SELECT h.tipo, COUNT(h) FROM Habitacion h GROUP BY h.tipo")
    List<Object[]> contarPorTipo();

    // Habitaciones de un tipo específico que estén disponibles
    @Query("SELECT h FROM Habitacion h WHERE h.tipo = :tipo AND h.estado = 'DISPONIBLE'")
    List<Habitacion> findDisponiblesByTipo(@Param("tipo") TipoHab tipo);

    // ─────────────────────────────────────────────
    // HABITACIONES ESPECÍFICAS
    // ─────────────────────────────────────────────

    // Buscar por número de habitación
    Habitacion findByNumero(String numero);

    // Habitaciones de un piso específico
    List<Habitacion> findByPiso(Integer piso);

    // Habitaciones con precio menor o igual a un valor
    @Query("SELECT h FROM Habitacion h WHERE h.precioNoche <= :precio AND h.estado = 'DISPONIBLE'")
    List<Habitacion> findDisponiblesByPrecioMaximo(@Param("precio") Double precio);

    // Habitaciones con capacidad mínima requerida
    @Query("SELECT h FROM Habitacion h WHERE h.capacidad >= :personas AND h.estado = 'DISPONIBLE'")
    List<Habitacion> findDisponiblesByCapacidad(@Param("personas") Integer personas);

}
