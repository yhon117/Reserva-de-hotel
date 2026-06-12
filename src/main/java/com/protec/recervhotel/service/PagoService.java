package com.protec.recervhotel.service;

import com.protec.recervhotel.dto.PagoCreacionDTO;
import com.protec.recervhotel.dto.PagoDTO;
import com.protec.recervhotel.entities.Factura;
import com.protec.recervhotel.entities.Pago;
import com.protec.recervhotel.entities.Reserva;
import com.protec.recervhotel.enums.EstadoPago;
import com.protec.recervhotel.enums.MetodoPago;
import com.protec.recervhotel.exception.BusinessException;
import com.protec.recervhotel.exception.ResourceNotFoundException;
import com.protec.recervhotel.mappers.PagoMapper;
import com.protec.recervhotel.persistencia.FacturaDao;
import com.protec.recervhotel.persistencia.PagoDao;
import com.protec.recervhotel.persistencia.ReservaDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PagoService {

    private final PagoDao pagoDao;
    private final ReservaDao reservaDao;
    private final FacturaDao facturaDao;
    private final PagoMapper pagoMapper;

    public PagoService(PagoDao pagoDao, ReservaDao reservaDao,
                       FacturaDao facturaDao, PagoMapper pagoMapper) {
        this.pagoDao = pagoDao;
        this.reservaDao = reservaDao;
        this.facturaDao = facturaDao;
        this.pagoMapper = pagoMapper;
    }

    @Transactional
    public PagoDTO registrarPago(PagoCreacionDTO dto) {
        Reserva reserva = reservaDao.findById(dto.getReservaId())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", dto.getReservaId()));

        MetodoPago metodo;
        try {
            metodo = MetodoPago.valueOf(dto.getMetodoPago().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Método de pago inválido: " + dto.getMetodoPago());
        }

        double totalPagado = pagoDao.findByReservaId(reserva.getId()).stream()
                .filter(p -> p.getEstadoPago() == EstadoPago.PAGADO)
                .mapToDouble(Pago::getMonto)
                .sum();

        double facturaTotal = facturaDao.findByReservaId(reserva.getId())
                .map(Factura::getTotal)
                .orElse(reserva.getTotal());

        double nuevoTotalPagado = totalPagado + dto.getMonto();

        EstadoPago estadoPago;
        if (nuevoTotalPagado >= facturaTotal) {
            estadoPago = EstadoPago.PAGADO;
        } else if (nuevoTotalPagado > 0) {
            estadoPago = EstadoPago.PARCIAL;
        } else {
            estadoPago = EstadoPago.PENDIENTE;
        }

        String codigo = "TXN-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" + String.format("%05d", System.currentTimeMillis() % 100000);

        Pago pago = Pago.builder()
                .reserva(reserva)
                .monto(dto.getMonto())
                .metodoPago(metodo)
                .estadoPago(estadoPago)
                .codigoTransaccion(codigo)
                .fechaPago(LocalDateTime.now())
                .observaciones(dto.getObservaciones())
                .build();

        pagoDao.save(pago);

        if (estadoPago == EstadoPago.PAGADO) {
            facturaDao.findByReservaId(reserva.getId()).ifPresent(f -> {
                f.setPagada(true);
                facturaDao.save(f);
            });
        }

        return pagoMapper.toDto(pago);
    }

    public List<PagoDTO> listarTodos() {
        return pagoMapper.toListDto(pagoDao.findAll());
    }

    public List<PagoDTO> listarPorReserva(Long reservaId) {
        return pagoMapper.toListDto(pagoDao.findByReservaId(reservaId));
    }

    public PagoDTO obtenerPorId(Long id) {
        Pago pago = pagoDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", id));
        return pagoMapper.toDto(pago);
    }
}
