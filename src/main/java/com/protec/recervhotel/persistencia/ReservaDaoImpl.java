package com.protec.recervhotel.persistencia;

import com.protec.recervhotel.entities.Reserva;
import com.protec.recervhotel.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class ReservaDaoImpl implements ReservaDao{


    @Autowired
    private ReservaRepository reservaRepository;

    @Override
    public void save(Reserva reserva) {

        reservaRepository.save(reserva);
    }

    @Override
    public Optional<Reserva> findById(Long id) {
        return reservaRepository.findById(id);
    }

    @Override
    public List<Reserva> findAll() {
        return (List<Reserva>) reservaRepository.findAll();
    }

    @Override
    public void delete(Long id) {

        reservaRepository.deleteById(id);
    }

    @Override
    public Long contarOcupadasEnFecha(LocalDate fecha) {
        return reservaRepository.contarOcupadasEnFecha(fecha);
    }

    @Override
    public Long contarHabitacionesOcupadasEnFecha(LocalDate fecha) {
        return reservaRepository.contarHabitacionesOcupadasEnFecha(fecha);
    }

    @Override
    public List<Reserva> findReservasEnRango(LocalDate inicio, LocalDate fin) {
        return reservaRepository.findReservasEnRango(inicio,fin);
    }

    @Override
    public List<Object[]> ocupacionPorDiaEnRango(LocalDate inicio, LocalDate fin) {
        return reservaRepository.ocupacionPorDiaEnRango(inicio,fin);
    }

    @Override
    public List<Object[]> ocupacionAgrupadaPorMes() {
        return reservaRepository.ocupacionAgrupadaPorMes();
    }

    @Override
    public List<Object[]> ingresosPorMes() {
        return reservaRepository.ingresosPorMes();
    }

    @Override
    public List<Object[]> tendenciaDesde(LocalDate desde) {
        return reservaRepository.tendenciaDesde(desde);
    }

    @Override
    public List<Reserva> findByHabitacionId(Long habitacionId) {
        return reservaRepository.findByHabitacionId(habitacionId);
    }

    @Override
    public List<Reserva> findByHabitacionEnRango(Long habitacionId, LocalDate inicio, LocalDate fin) {
        return reservaRepository.findByHabitacionEnRango(habitacionId,inicio,fin);
    }

    @Override
    public List<Object[]> habitacionesMasReservadas() {
        return reservaRepository.habitacionesMasReservadas();
    }
}
