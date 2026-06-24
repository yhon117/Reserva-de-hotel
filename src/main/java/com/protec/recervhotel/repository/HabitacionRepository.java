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


    @Query("SELECT h.estado, COUNT(h) FROM Habitacion h GROUP BY h.estado")
    List<Object[]> contarPorEstado();

    List<Habitacion> findByEstado(EstadoHab estado);

    @Query("SELECT h.tipo, COUNT(h) FROM Habitacion h GROUP BY h.tipo")
    List<Object[]> contarPorTipo();

    @Query("SELECT h FROM Habitacion h WHERE h.tipo = :tipo AND h.estado = 'DISPONIBLE'")
    List<Habitacion> findDisponiblesByTipo(@Param("tipo") TipoHab tipo);

    Habitacion findByNumero(String numero);

    List<Habitacion> findByPiso(Integer piso);

    @Query("SELECT h FROM Habitacion h WHERE h.precioNoche <= :precio AND h.estado = 'DISPONIBLE'")
    List<Habitacion> findDisponiblesByPrecioMaximo(@Param("precio") Double precio);

    @Query("SELECT h FROM Habitacion h WHERE h.capacidad >= :personas AND h.estado = 'DISPONIBLE'")
    List<Habitacion> findDisponiblesByCapacidad(@Param("personas") Integer personas);

}
