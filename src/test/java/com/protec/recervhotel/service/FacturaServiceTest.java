package com.protec.recervhotel.service;

import com.protec.recervhotel.dto.FacturaDTO;
import com.protec.recervhotel.dto.FacturaResumenDTO;
import com.protec.recervhotel.entities.*;
import com.protec.recervhotel.enums.*;
import com.protec.recervhotel.exception.ResourceNotFoundException;
import com.protec.recervhotel.mappers.FacturaMapper;
import com.protec.recervhotel.persistencia.FacturaDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FacturaServiceTest {

    @Mock
    private FacturaDao facturaDao;
    @Mock
    private FacturaMapper facturaMapper;

    @InjectMocks
    private FacturaService facturaService;

    private Reserva reserva;
    private Factura factura;
    private FacturaDTO facturaDTO;

    @BeforeEach
    void setUp() {
        Usuario usuario = Usuario.builder().id(1L).nombre("Juan").email("juan@test.com").build();
        Habitacion habitacion = Habitacion.builder().id(1L).numero("101")
                .precioNoche(100.0).tipo(TipoHab.SIMPLE).build();
        reserva = Reserva.builder().id(1L).usuario(usuario).habitacion(habitacion)
                .fechaEntrada(LocalDate.now().plusDays(1))
                .fechaSalida(LocalDate.now().plusDays(4))
                .total(300.0).build();

        factura = Factura.builder().id(1L).reserva(reserva)
                .numeroFactura("FAC-20260612-00001")
                .subtotal(300.0).iva(57.0).total(357.0).pagada(false)
                .fechaEmision(java.time.LocalDateTime.now()).build();

        facturaDTO = FacturaDTO.builder().id(1L).numeroFactura("FAC-20260612-00001")
                .reservaId(1L).huespedNombre("Juan").build();

        ReflectionTestUtils.setField(facturaService, "ivaPorcentaje", 19);
        ReflectionTestUtils.setField(facturaService, "hotelNombre", "RecervHotel");
        ReflectionTestUtils.setField(facturaService, "hotelDireccion", "");
        ReflectionTestUtils.setField(facturaService, "hotelTelefono", "");
        ReflectionTestUtils.setField(facturaService, "hotelEmail", "");
    }

    @Test
    void generarFactura_CreatesFacturaWithItems() {
        when(facturaDao.existsByReservaId(1L)).thenReturn(false);
        when(facturaDao.findAll()).thenReturn(List.of());

        Factura result = facturaService.generarFactura(reserva);

        assertNotNull(result);
        assertEquals(300.0, result.getSubtotal());
        assertEquals(57.0, result.getIva());
        assertEquals(357.0, result.getTotal());
        assertFalse(result.getPagada());
        assertEquals(1, result.getItems().size());
        assertEquals((int) 3, result.getItems().get(0).getCantidad());
        assertEquals(100.0, result.getItems().get(0).getPrecioUnitario());
        verify(facturaDao).save(any());
    }

    @Test
    void generarFactura_WhenAlreadyExists_ReturnsExisting() {
        when(facturaDao.existsByReservaId(1L)).thenReturn(true);
        when(facturaDao.findByReservaId(1L)).thenReturn(Optional.of(factura));

        Factura result = facturaService.generarFactura(reserva);

        assertNotNull(result);
        assertEquals("FAC-20260612-00001", result.getNumeroFactura());
        verify(facturaDao, never()).save(any());
    }

    @Test
    void obtenerPorId_WhenExists_ReturnsFactura() {
        when(facturaDao.findById(1L)).thenReturn(Optional.of(factura));
        when(facturaMapper.toDto(factura)).thenReturn(facturaDTO);

        FacturaDTO result = facturaService.obtenerPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void obtenerPorId_WhenNotExists_ThrowsException() {
        when(facturaDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> facturaService.obtenerPorId(99L));
    }

    @Test
    void obtenerPorReservaId_WhenExists_ReturnsFactura() {
        when(facturaDao.findByReservaId(1L)).thenReturn(Optional.of(factura));
        when(facturaMapper.toDto(factura)).thenReturn(facturaDTO);

        FacturaDTO result = facturaService.obtenerPorReservaId(1L);

        assertNotNull(result);
    }

    @Test
    void obtenerPorReservaId_WhenNotExists_ThrowsException() {
        when(facturaDao.findByReservaId(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> facturaService.obtenerPorReservaId(99L));
    }

    @Test
    void listarTodas_ReturnsAll() {
        FacturaResumenDTO resumen = FacturaResumenDTO.builder()
                .id(1L).numeroFactura("FAC-001").build();
        when(facturaDao.findAll()).thenReturn(List.of(factura));
        when(facturaMapper.toListResumenDto(List.of(factura))).thenReturn(List.of(resumen));

        List<FacturaResumenDTO> results = facturaService.listarTodas();

        assertEquals(1, results.size());
    }

    @Test
    void generarPdf_WhenExists_ReturnsBytes() {
        when(facturaDao.findById(1L)).thenReturn(Optional.of(factura));

        byte[] pdf = facturaService.generarPdf(1L);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }

    @Test
    void generarPdf_WhenNotExists_ThrowsException() {
        when(facturaDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> facturaService.generarPdf(99L));
    }
}
