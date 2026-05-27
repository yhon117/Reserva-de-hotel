package com.protec.recervhotel.persistencia;

import com.protec.recervhotel.emun.EstadoHab;
import com.protec.recervhotel.emun.TipoHab;
import com.protec.recervhotel.entitys.Habitacion;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HabitacionDao {

    void save(Habitacion habitacion);

    Optional<Habitacion> findById(Long id);

    List<Habitacion>findAll();

    void delete(Long id);

    List<Object[]> contarPorEstado();

    List<Habitacion> findByEstado(EstadoHab estado);

    List<Object[]> contarPorTipo();

    List<Habitacion> findDisponiblesByTipo(TipoHab tipo);

    Habitacion findByNumero(String numero);

    List<Habitacion> findByPiso(Integer piso);

    List<Habitacion> findDisponiblesByPrecioMaximo( Double precio);

    List<Habitacion> findDisponiblesByCapacidad(Integer personas);

}
