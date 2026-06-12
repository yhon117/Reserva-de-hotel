package com.protec.recervhotel.mappers;

import com.protec.recervhotel.dto.FacturaDTO;
import com.protec.recervhotel.dto.FacturaItemDTO;
import com.protec.recervhotel.dto.FacturaResumenDTO;
import com.protec.recervhotel.entities.Factura;
import com.protec.recervhotel.entities.FacturaItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Mapper(componentModel = "spring", imports = {ChronoUnit.class})
public interface FacturaMapper {

    @Mapping(target = "reservaId", source = "reserva.id")
    @Mapping(target = "huespedNombre", source = "reserva.usuario.nombre")
    @Mapping(target = "huespedEmail", source = "reserva.usuario.email")
    @Mapping(target = "habitacionNumero", source = "reserva.habitacion.numero")
    @Mapping(target = "habitacionTipo", source = "reserva.habitacion.tipo")
    @Mapping(target = "fechaEntrada", source = "reserva.fechaEntrada")
    @Mapping(target = "fechaSalida", source = "reserva.fechaSalida")
    @Mapping(target = "noches", expression = "java(ChronoUnit.DAYS.between(factura.getReserva().getFechaEntrada(), factura.getReserva().getFechaSalida()))")
    FacturaDTO toDto(Factura factura);

    List<FacturaDTO> toListDto(List<Factura> facturas);

    @Mapping(target = "reservaId", source = "reserva.id")
    @Mapping(target = "huespedNombre", source = "reserva.usuario.nombre")
    @Mapping(target = "habitacionNumero", source = "reserva.habitacion.numero")
    FacturaResumenDTO toResumenDto(Factura factura);

    List<FacturaResumenDTO> toListResumenDto(List<Factura> facturas);

    FacturaItemDTO toItemDto(FacturaItem item);

    List<FacturaItemDTO> toListItemDto(List<FacturaItem> items);
}
