package com.protec.recervhotel.persistencia;

import com.protec.recervhotel.emun.EstadoHab;
import com.protec.recervhotel.emun.TipoHab;
import com.protec.recervhotel.entitys.Habitacion;
import com.protec.recervhotel.repository.HabitacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class HabitacionDaoImpl implements HabitacionDao{

    @Autowired
    private HabitacionRepository habitacionRepository;

    @Override
    public void save(Habitacion habitacion) {

        habitacionRepository.save(habitacion);
    }

    @Override
    public Optional<Habitacion> findById(Long id) {
        return habitacionRepository.findById(id);
    }

    @Override
    public List<Habitacion> findAll() {
        return (List<Habitacion>) habitacionRepository.findAll();
    }

    @Override
    public void delete(Long id) {

        habitacionRepository.deleteById(id);
    }

    @Override
    public List<Object[]> contarPorEstado() {
        return habitacionRepository.contarPorEstado();
    }

    @Override
    public List<Habitacion> findByEstado(EstadoHab estado) {
        return habitacionRepository.findByEstado(estado);
    }

    @Override
    public List<Object[]> contarPorTipo() {
        return habitacionRepository.contarPorTipo();
    }

    @Override
    public List<Habitacion> findDisponiblesByTipo(TipoHab tipo) {
        return habitacionRepository.findDisponiblesByTipo(tipo);
    }

    @Override
    public Habitacion findByNumero(String numero) {
        return habitacionRepository.findByNumero(numero);
    }

    @Override
    public List<Habitacion> findByPiso(Integer piso) {
        return habitacionRepository.findByPiso(piso);
    }

    @Override
    public List<Habitacion> findDisponiblesByPrecioMaximo(Double precio) {
        return habitacionRepository.findDisponiblesByPrecioMaximo(precio);
    }

    @Override
    public List<Habitacion> findDisponiblesByCapacidad(Integer personas) {
        return habitacionRepository.findDisponiblesByCapacidad(personas);
    }
}
