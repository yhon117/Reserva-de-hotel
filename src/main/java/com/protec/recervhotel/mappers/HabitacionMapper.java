package com.protec.recervhotel.mappers;

import com.protec.recervhotel.dto.HabitacionCreacionDTO;
import com.protec.recervhotel.dto.HabitacionDTO;
import com.protec.recervhotel.entities.Habitacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HabitacionMapper {

    @Mapping(target = "tipo", expression = "java(habitacion.getTipo().name())")
    @Mapping(target = "estado", expression = "java(habitacion.getEstado().name())")
    HabitacionDTO toDto(Habitacion habitacion);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reservas", ignore = true)
    @Mapping(target = "tipo", expression = "java(com.protec.recervhotel.enums.TipoHab.valueOf(dto.getTipo().toUpperCase()))")
    @Mapping(target = "estado", expression = "java(com.protec.recervhotel.enums.EstadoHab.valueOf(dto.getEstado().toUpperCase()))")
    Habitacion toEntity(HabitacionCreacionDTO dto);

    List<HabitacionDTO> toListDto(List<Habitacion> habitaciones);
}
