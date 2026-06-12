package com.protec.recervhotel.mappers;

import com.protec.recervhotel.dto.PagoDTO;
import com.protec.recervhotel.entities.Pago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PagoMapper {

    @Mapping(target = "reservaId", source = "reserva.id")
    @Mapping(target = "huespedNombre", source = "reserva.usuario.nombre")
    @Mapping(target = "habitacionNumero", source = "reserva.habitacion.numero")
    @Mapping(target = "metodoPago", expression = "java(pago.getMetodoPago().name())")
    @Mapping(target = "estadoPago", expression = "java(pago.getEstadoPago().name())")
    PagoDTO toDto(Pago pago);

    List<PagoDTO> toListDto(List<Pago> pagos);
}
