package com.protec.recervhotel.service;

import com.protec.recervhotel.dto.HabitacionCreacionDTO;
import com.protec.recervhotel.dto.HabitacionDTO;
import com.protec.recervhotel.enums.EstadoHab;
import com.protec.recervhotel.enums.TipoHab;
import com.protec.recervhotel.exception.BusinessException;
import com.protec.recervhotel.exception.ResourceNotFoundException;
import com.protec.recervhotel.mappers.HabitacionMapper;
import com.protec.recervhotel.persistencia.HabitacionDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HabitacionService {

    private final HabitacionDao habitacionDao;
    private final HabitacionMapper habitacionMapper;

    public HabitacionService(HabitacionDao habitacionDao, HabitacionMapper habitacionMapper) {
        this.habitacionDao = habitacionDao;
        this.habitacionMapper = habitacionMapper;
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
        return habitacionMapper.toDto(habitacion);
    }

    public List<HabitacionDTO> listarTodas() {
        return habitacionMapper.toListDto(habitacionDao.findAll());
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
        return habitacionMapper.toListDto(habitacionDao.findDisponiblesByTipo(tipoHab));
    }

    public List<HabitacionDTO> buscarDisponiblesPorPrecioMaximo(Double precio) {
        return habitacionMapper.toListDto(habitacionDao.findDisponiblesByPrecioMaximo(precio));
    }

    public List<HabitacionDTO> buscarDisponiblesPorCapacidad(Integer personas) {
        return habitacionMapper.toListDto(habitacionDao.findDisponiblesByCapacidad(personas));
    }
}
