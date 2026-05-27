package com.protec.recervhotel.persistencia;

import com.protec.recervhotel.entitys.Reserva;
import com.protec.recervhotel.repository.ReservaReposistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class ReservaDaoImpl implements ReservaDao{


    @Autowired
    private ReservaReposistory reservaReposistory;

    @Override
    public void save(Reserva reserva) {

        reservaReposistory.save(reserva);
    }

    @Override
    public Optional<Reserva> findById(Long id) {
        return reservaReposistory.findById(id);
    }

    @Override
    public List<Reserva> findAll() {
        return (List<Reserva>) reservaReposistory.findAll();
    }

    @Override
    public void delete(Long id) {

        reservaReposistory.deleteById(id);
    }

    @Override
    public Long contarOcupadasEnFecha(LocalDate fecha) {
        return reservaReposistory.contarOcupadasEnFecha(fecha);
    }

    @Override
    public Long contarHabitacionesOcupadasEnFecha(LocalDate fecha) {
        return reservaReposistory.contarHabitacionesOcupadasEnFecha(fecha);
    }

    @Override
    public List<Reserva> findReservasEnRango(LocalDate inicio, LocalDate fin) {
        return reservaReposistory.findReservasEnRango(inicio,fin);
    }

    @Override
    public List<Object[]> ocupacionPorDiaEnRango(LocalDate inicio, LocalDate fin) {
        return reservaReposistory.ocupacionPorDiaEnRango(inicio,fin);
    }

    @Override
    public List<Object[]> ocupacionAgrupadaPorMes() {
        return reservaReposistory.ocupacionAgrupadaPorMes();
    }

    @Override
    public List<Object[]> ingresosPorMes() {
        return reservaReposistory.ingresosPorMes();
    }

    @Override
    public List<Object[]> tendenciaDesde(LocalDate desde) {
        return reservaReposistory.tendenciaDesde(desde);
    }

    @Override
    public List<Reserva> findByHabitacionId(Long habitacionId) {
        return reservaReposistory.findByHabitacionId(habitacionId);
    }

    @Override
    public List<Reserva> findByHabitacionEnRango(Long habitacionId, LocalDate inicio, LocalDate fin) {
        return reservaReposistory.findByHabitacionEnRango(habitacionId,inicio,fin);
    }
}
