package com.protec.recervhotel.service;

import com.protec.recervhotel.dto.ReservaCreacionDTO;
import com.protec.recervhotel.dto.ReservaDTO;
import com.protec.recervhotel.entities.Factura;
import com.protec.recervhotel.entities.Habitacion;
import com.protec.recervhotel.entities.Reserva;
import com.protec.recervhotel.entities.Usuario;
import com.protec.recervhotel.enums.Estado;
import com.protec.recervhotel.enums.EstadoHab;
import com.protec.recervhotel.exception.BusinessException;
import com.protec.recervhotel.exception.ResourceNotFoundException;
import com.protec.recervhotel.mappers.ReservaMapper;
import com.protec.recervhotel.persistencia.HabitacionDao;
import com.protec.recervhotel.persistencia.ReservaDao;
import com.protec.recervhotel.persistencia.UsuarioDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class ReservaService {

    private final ReservaDao reservaDao;
    private final UsuarioDao usuarioDao;
    private final HabitacionDao habitacionDao;
    private final ReservaMapper reservaMapper;
    private final FacturaService facturaService;

    public ReservaService(ReservaDao reservaDao, UsuarioDao usuarioDao,
                          HabitacionDao habitacionDao, ReservaMapper reservaMapper,
                          FacturaService facturaService) {
        this.reservaDao = reservaDao;
        this.usuarioDao = usuarioDao;
        this.habitacionDao = habitacionDao;
        this.reservaMapper = reservaMapper;
        this.facturaService = facturaService;
    }

    @Transactional
    public ReservaDTO crear(ReservaCreacionDTO dto) {
        Usuario usuario = usuarioDao.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", dto.getUsuarioId()));
        Habitacion habitacion = habitacionDao.findById(dto.getHabitacionId())
                .orElseThrow(() -> new ResourceNotFoundException("Habitación", dto.getHabitacionId()));

        if (habitacion.getEstado() != EstadoHab.DISPONIBLE) {
            throw new BusinessException("La habitación no está disponible");
        }
        if (dto.getFechaEntrada().isBefore(LocalDate.now())) {
            throw new BusinessException("La fecha de entrada no puede ser anterior a hoy");
        }
        if (!dto.getFechaSalida().isAfter(dto.getFechaEntrada())) {
            throw new BusinessException("La fecha de salida debe ser posterior a la de entrada");
        }

        List<Reserva> conflictos = reservaDao.findByHabitacionEnRango(
                dto.getHabitacionId(), dto.getFechaEntrada(), dto.getFechaSalida());
        boolean hayConflicto = conflictos.stream()
                .anyMatch(r -> r.getEstado() == Estado.CONFIRMADA || r.getEstado() == Estado.PENDIENTE);
        if (hayConflicto) {
            throw new BusinessException("La habitación ya está reservada en esas fechas");
        }

        long noches = ChronoUnit.DAYS.between(dto.getFechaEntrada(), dto.getFechaSalida());
        double total = noches * habitacion.getPrecioNoche();

        Reserva reserva = reservaMapper.toEntity(dto);
        reserva.setUsuario(usuario);
        reserva.setHabitacion(habitacion);
        reserva.setTotal(total);
        reserva.setEstado(Estado.CONFIRMADA);

        reservaDao.save(reserva);
        facturaService.generarFactura(reserva);
        return reservaMapper.toDto(reserva);
    }

    @Transactional
    public ReservaDTO cancelar(Long id) {
        Reserva reserva = reservaDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));
        if (reserva.getEstado() == Estado.CANCELADA) {
            throw new BusinessException("La reserva ya está cancelada");
        }
        reserva.setEstado(Estado.CANCELADA);
        reservaDao.save(reserva);
        return reservaMapper.toDto(reserva);
    }

    public ReservaDTO obtenerPorId(Long id) {
        Reserva reserva = reservaDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));
        return reservaMapper.toDto(reserva);
    }

    public List<ReservaDTO> listarTodas() {
        return reservaMapper.toListDto(reservaDao.findAll());
    }

    public List<ReservaDTO> listarPorHabitacion(Long habitacionId) {
        if (habitacionDao.findById(habitacionId).isEmpty()) {
            throw new ResourceNotFoundException("Habitación", habitacionId);
        }
        return reservaMapper.toListDto(reservaDao.findByHabitacionId(habitacionId));
    }

    public Long contarOcupadasEnFecha(LocalDate fecha) {
        return reservaDao.contarOcupadasEnFecha(fecha);
    }

    public List<Map<String, Object>> ingresosPorMes() {
        return reservaDao.ingresosPorMes().stream()
                .map(row -> Map.of("mes", row[0], "anio", row[1], "total", row[2]))
                .toList();
    }

    public List<Map<String, Object>> ocupacionAgrupadaPorMes() {
        return reservaDao.ocupacionAgrupadaPorMes().stream()
                .map(row -> Map.of("mes", row[0], "anio", row[1], "cantidad", row[2]))
                .toList();
    }

    public List<Map<String, Object>> tendenciaDesde(LocalDate desde) {
        return reservaDao.tendenciaDesde(desde).stream()
                .map(row -> Map.of("fecha", row[0], "cantidad", row[1]))
                .toList();
    }

    public List<Map<String, Object>> habitacionesMasReservadas() {
        return reservaDao.habitacionesMasReservadas().stream()
                .map(row -> Map.of("habitacionId", row[0], "numero", row[1], "total", row[2]))
                .toList();
    }
}
