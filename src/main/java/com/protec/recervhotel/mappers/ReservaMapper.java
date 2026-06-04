package com.protec.recervhotel.mappers;

import com.protec.recervhotel.dto.ReservaCreacionDTO;
import com.protec.recervhotel.dto.ReservaDTO;
import com.protec.recervhotel.entities.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    @Mapping(target = "estado", expression = "java(reserva.getEstado().name())")
    @Mapping(target = "usuarioId", source = "reserva.usuario.id")
    @Mapping(target = "habitacionId", source = "reserva.habitacion.id")
    @Mapping(target = "usuarioNombre", source = "reserva.usuario.nombre")
    @Mapping(target = "habitacionNumero", source = "reserva.habitacion.numero")
    ReservaDTO toDto(Reserva reserva);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "habitacion", ignore = true)
    Reserva toEntity(ReservaCreacionDTO dto);

    List<ReservaDTO> toListDto(List<Reserva> reservas);
}
