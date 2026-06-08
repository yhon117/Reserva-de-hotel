package com.protec.recervhotel.service;

import com.protec.recervhotel.dto.HabitacionCreacionDTO;
import com.protec.recervhotel.dto.HabitacionDTO;
import com.protec.recervhotel.enums.EstadoHab;
import com.protec.recervhotel.enums.TipoHab;
import com.protec.recervhotel.exception.BusinessException;
import com.protec.recervhotel.exception.ResourceNotFoundException;
import com.protec.recervhotel.mappers.HabitacionMapper;
import com.protec.recervhotel.persistencia.HabitacionDao;
import com.protec.recervhotel.persistencia.ReservaDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class HabitacionService {

    private final HabitacionDao habitacionDao;
    private final HabitacionMapper habitacionMapper;
    private final ReservaDao reservaDao;

    public HabitacionService(HabitacionDao habitacionDao, HabitacionMapper habitacionMapper, ReservaDao reservaDao) {
        this.habitacionDao = habitacionDao;
        this.habitacionMapper = habitacionMapper;
        this.reservaDao = reservaDao;
    }

    private Set<Long> ocupadasHoy() {
        return new HashSet<>(reservaDao.findHabitacionesOcupadasEnFecha(LocalDate.now()));
    }

    private String estadoEfectivo(EstadoHab estadoBD, Long habitacionId, Set<Long> ocupadas) {
        if (estadoBD == EstadoHab.MANTENIMIENTO) return EstadoHab.MANTENIMIENTO.name();
        if (ocupadas.contains(habitacionId)) return "OCUPADA";
        return EstadoHab.DISPONIBLE.name();
    }

    @Transactional
    public HabitacionDTO crear(HabitacionCreacionDTO dto) {
        if (habitacionDao.findByNumero(dto.getNumero()) != null) {
            throw new BusinessException("Ya existe una habitación con el número " + dto.getNumero());
        }
        var habitacion = habitacionMapper.toEntity(dto);
        habitacionDao.save(habitacion);
        return habitacionMapper.toDto(habitacion);
    }

    public HabitacionDTO obtenerPorId(Long id) {
        var habitacion = habitacionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habitación", id));
        var dto = habitacionMapper.toDto(habitacion);
        dto.setEstado(estadoEfectivo(habitacion.getEstado(), id, ocupadasHoy()));
        return dto;
    }

    public List<HabitacionDTO> listarTodas() {
        var habitaciones = habitacionDao.findAll();
        var ocupadas = ocupadasHoy();
        return habitacionMapper.toListDto(habitaciones).stream()
                .peek(dto -> {
                    var h = habitaciones.stream()
                            .filter(hh -> hh.getId().equals(dto.getId()))
                            .findFirst().orElse(null);
                    if (h != null) {
                        dto.setEstado(estadoEfectivo(h.getEstado(), dto.getId(), ocupadas));
                    }
                })
                .toList();
    }

    @Transactional
    public HabitacionDTO actualizar(Long id, HabitacionCreacionDTO dto) {
        var habitacion = habitacionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habitación", id));
        var existeMismoNumero = habitacionDao.findByNumero(dto.getNumero());
        if (existeMismoNumero != null && !existeMismoNumero.getId().equals(id)) {
            throw new BusinessException("Ya existe otra habitación con el número " + dto.getNumero());
        }
        habitacion.setNumero(dto.getNumero());
        habitacion.setPiso(dto.getPiso());
        habitacion.setPrecioNoche(dto.getPrecioNoche());
        habitacion.setCapacidad(dto.getCapacidad());
        habitacion.setTipo(TipoHab.valueOf(dto.getTipo()));
        habitacion.setEstado(EstadoHab.valueOf(dto.getEstado()));
        habitacionDao.save(habitacion);
        return habitacionMapper.toDto(habitacion);
    }

    @Transactional
    public void eliminar(Long id) {
        if (habitacionDao.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Habitación", id);
        }
        habitacionDao.delete(id);
    }

    public List<HabitacionDTO> buscarDisponiblesPorTipo(String tipo) {
        TipoHab tipoHab;
        try {
            tipoHab = TipoHab.valueOf(tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Tipo de habitación inválido: " + tipo);
        }
        var ocupadas = ocupadasHoy();
        return habitacionMapper.toListDto(habitacionDao.findDisponiblesByTipo(tipoHab)).stream()
                .filter(dto -> !ocupadas.contains(dto.getId()))
                .toList();
    }

    public List<HabitacionDTO> buscarDisponiblesPorPrecioMaximo(Double precio) {
        var ocupadas = ocupadasHoy();
        return habitacionMapper.toListDto(habitacionDao.findDisponiblesByPrecioMaximo(precio)).stream()
                .filter(dto -> !ocupadas.contains(dto.getId()))
                .toList();
    }

    public List<HabitacionDTO> buscarDisponiblesPorCapacidad(Integer personas) {
        var ocupadas = ocupadasHoy();
        return habitacionMapper.toListDto(habitacionDao.findDisponiblesByCapacidad(personas)).stream()
                .filter(dto -> !ocupadas.contains(dto.getId()))
                .toList();
    }
}
