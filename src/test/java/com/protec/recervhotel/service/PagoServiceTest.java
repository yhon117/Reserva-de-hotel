package com.protec.recervhotel.service;

import com.protec.recervhotel.dto.PagoCreacionDTO;
import com.protec.recervhotel.dto.PagoDTO;
import com.protec.recervhotel.entities.*;
import com.protec.recervhotel.enums.*;
import com.protec.recervhotel.exception.BusinessException;
import com.protec.recervhotel.exception.ResourceNotFoundException;
import com.protec.recervhotel.mappers.PagoMapper;
import com.protec.recervhotel.persistencia.FacturaDao;
import com.protec.recervhotel.persistencia.PagoDao;
import com.protec.recervhotel.persistencia.ReservaDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoDao pagoDao;
    @Mock
    private ReservaDao reservaDao;
    @Mock
    private FacturaDao facturaDao;
    @Mock
    private PagoMapper pagoMapper;

    @InjectMocks
    private PagoService pagoService;

    private Reserva reserva;
    private Pago pago;
    private PagoDTO pagoDTO;
    private Factura factura;

    @BeforeEach
    void setUp() {
        Usuario usuario = Usuario.builder().id(1L).nombre("Juan").build();
        Habitacion habitacion = Habitacion.builder().id(1L).numero("101").precioNoche(100.0).build();
        reserva = Reserva.builder().id(1L).usuario(usuario).habitacion(habitacion)
                .total(400.0).build();

        pago = Pago.builder().id(1L).reserva(reserva).monto(400.0)
                .metodoPago(MetodoPago.EFECTIVO).estadoPago(EstadoPago.PAGADO)
                .codigoTransaccion("TXN-001").build();

        pagoDTO = PagoDTO.builder().id(1L).reservaId(1L).monto(400.0)
                .metodoPago("EFECTIVO").estadoPago("PAGADO").build();

        factura = Factura.builder().id(1L).reserva(reserva).total(400.0).pagada(false).build();
    }

    @Test
    void registrarPago_WithFullAmount_MarksFacturaPagada() {
        when(reservaDao.findById(1L)).thenReturn(Optional.of(reserva));
        when(pagoDao.findByReservaId(1L)).thenReturn(List.of());
        when(facturaDao.findByReservaId(1L)).thenReturn(Optional.of(factura));
        when(pagoMapper.toDto(any())).thenReturn(pagoDTO);

        PagoCreacionDTO dto = PagoCreacionDTO.builder()
                .reservaId(1L).monto(400.0).metodoPago("EFECTIVO").build();

        PagoDTO result = pagoService.registrarPago(dto);

        assertNotNull(result);
        verify(pagoDao).save(any());
        assertTrue(factura.getPagada());
    }

    @Test
    void registrarPago_WithPartialAmount_KeepsFacturaPendiente() {
        when(reservaDao.findById(1L)).thenReturn(Optional.of(reserva));
        when(pagoDao.findByReservaId(1L)).thenReturn(List.of());

        PagoCreacionDTO dto = PagoCreacionDTO.builder()
                .reservaId(1L).monto(200.0).metodoPago("TARJETA_CREDITO").build();

        pagoService.registrarPago(dto);

        verify(pagoDao).save(any());
        verify(facturaDao, never()).save(any());
    }

    @Test
    void registrarPago_WhenReservaNotExists_ThrowsException() {
        when(reservaDao.findById(99L)).thenReturn(Optional.empty());

        PagoCreacionDTO dto = PagoCreacionDTO.builder()
                .reservaId(99L).monto(100.0).metodoPago("EFECTIVO").build();

        assertThrows(ResourceNotFoundException.class, () -> pagoService.registrarPago(dto));
    }

    @Test
    void registrarPago_WithInvalidMetodo_ThrowsException() {
        when(reservaDao.findById(1L)).thenReturn(Optional.of(reserva));

        PagoCreacionDTO dto = PagoCreacionDTO.builder()
                .reservaId(1L).monto(100.0).metodoPago("INVALIDO").build();

        assertThrows(BusinessException.class, () -> pagoService.registrarPago(dto));
    }

    @Test
    void listarTodos_ReturnsAll() {
        when(pagoDao.findAll()).thenReturn(List.of(pago));
        when(pagoMapper.toListDto(List.of(pago))).thenReturn(List.of(pagoDTO));

        List<PagoDTO> results = pagoService.listarTodos();

        assertEquals(1, results.size());
    }

    @Test
    void listarPorReserva_ReturnsPagos() {
        when(pagoDao.findByReservaId(1L)).thenReturn(List.of(pago));
        when(pagoMapper.toListDto(List.of(pago))).thenReturn(List.of(pagoDTO));

        List<PagoDTO> results = pagoService.listarPorReserva(1L);

        assertEquals(1, results.size());
    }

    @Test
    void obtenerPorId_WhenExists_ReturnsPago() {
        when(pagoDao.findById(1L)).thenReturn(Optional.of(pago));
        when(pagoMapper.toDto(pago)).thenReturn(pagoDTO);

        PagoDTO result = pagoService.obtenerPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void obtenerPorId_WhenNotExists_ThrowsException() {
        when(pagoDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pagoService.obtenerPorId(99L));
    }
}
