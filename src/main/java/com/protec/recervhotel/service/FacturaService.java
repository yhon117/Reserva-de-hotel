package com.protec.recervhotel.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.protec.recervhotel.dto.FacturaDTO;
import com.protec.recervhotel.dto.FacturaResumenDTO;
import com.protec.recervhotel.entities.Factura;
import com.protec.recervhotel.entities.FacturaItem;
import com.protec.recervhotel.entities.Reserva;
import com.protec.recervhotel.entities.Usuario;
import com.protec.recervhotel.exception.ResourceNotFoundException;
import com.protec.recervhotel.mappers.FacturaMapper;
import com.protec.recervhotel.persistencia.FacturaDao;
import com.protec.recervhotel.persistencia.UsuarioDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class FacturaService {

    private final FacturaDao facturaDao;
    private final FacturaMapper facturaMapper;
    private final UsuarioDao usuarioDao;

    @Value("${factura.iva:19}")
    private int ivaPorcentaje;

    @Value("${factura.hotel.nombre:RecervHotel}")
    private String hotelNombre;

    @Value("${factura.hotel.direccion:}")
    private String hotelDireccion;

    @Value("${factura.hotel.telefono:}")
    private String hotelTelefono;

    @Value("${factura.hotel.email:}")
    private String hotelEmail;

    public FacturaService(FacturaDao facturaDao, FacturaMapper facturaMapper, UsuarioDao usuarioDao) {
        this.facturaDao = facturaDao;
        this.facturaMapper = facturaMapper;
        this.usuarioDao = usuarioDao;
    }

    @Transactional
    public Factura generarFactura(Reserva reserva) {
        if (facturaDao.existsByReservaId(reserva.getId())) {
            return facturaDao.findByReservaId(reserva.getId()).orElseThrow();
        }

        long noches = ChronoUnit.DAYS.between(reserva.getFechaEntrada(), reserva.getFechaSalida());
        double precioNoche = reserva.getHabitacion().getPrecioNoche();
        double subtotal = noches * precioNoche;
        double iva = subtotal * ivaPorcentaje / 100.0;
        double total = subtotal + iva;

        String numeroFactura = generarNumeroFactura();

        Factura factura = Factura.builder()
                .reserva(reserva)
                .numeroFactura(numeroFactura)
                .subtotal(subtotal)
                .iva(iva)
                .total(total)
                .fechaEmision(LocalDateTime.now())
                .pagada(false)
                .build();

        FacturaItem item = FacturaItem.builder()
                .factura(factura)
                .descripcion("Alojamiento - Hab. " + reserva.getHabitacion().getNumero()
                        + " (" + reserva.getHabitacion().getTipo().name() + ")")
                .cantidad((int) noches)
                .precioUnitario(precioNoche)
                .total(subtotal)
                .build();

        factura.setItems(List.of(item));
        facturaDao.save(factura);
        return factura;
    }

    public FacturaDTO obtenerPorId(Long id) {
        Factura factura = facturaDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura", id));
        return facturaMapper.toDto(factura);
    }

    public FacturaDTO obtenerPorReservaId(Long reservaId) {
        Factura factura = facturaDao.findByReservaId(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Factura para reserva", reservaId));
        return facturaMapper.toDto(factura);
    }

    public List<FacturaResumenDTO> listarTodas() {
        return facturaMapper.toListResumenDto(facturaDao.findAll());
    }

    public List<FacturaResumenDTO> listarPorEmailUsuario(String email) {
        Usuario usuario = usuarioDao.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", 0L));
        return facturaMapper.toListResumenDto(facturaDao.findByUsuarioId(usuario.getId()));
    }

    public byte[] generarPdf(Long facturaId) {
        Factura factura = facturaDao.findById(facturaId)
                .orElseThrow(() -> new ResourceNotFoundException("Factura", facturaId));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);

        Paragraph title = new Paragraph(hotelNombre, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        if (!hotelDireccion.isEmpty()) {
            document.add(new Paragraph(hotelDireccion, normalFont));
        }
        if (!hotelTelefono.isEmpty()) {
            document.add(new Paragraph("Tel: " + hotelTelefono, normalFont));
        }
        if (!hotelEmail.isEmpty()) {
            document.add(new Paragraph("Email: " + hotelEmail, normalFont));
        }

        document.add(new Paragraph(" "));
        Paragraph facturaTitle = new Paragraph("FACTURA " + factura.getNumeroFactura(), headerFont);
        facturaTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(facturaTitle);

        document.add(new Paragraph("Fecha de emisión: " + factura.getFechaEmision()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), normalFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("DATOS DEL HUÉSPED", headerFont));
        document.add(new Paragraph("Nombre: " + factura.getReserva().getUsuario().getNombre(), normalFont));
        document.add(new Paragraph("Email: " + factura.getReserva().getUsuario().getEmail(), normalFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("DATOS DE LA RESERVA", headerFont));
        document.add(new Paragraph("N° Reserva: " + factura.getReserva().getId(), normalFont));
        document.add(new Paragraph("Habitación: " + factura.getReserva().getHabitacion().getNumero()
                + " (" + factura.getReserva().getHabitacion().getTipo().name() + ")", normalFont));
        document.add(new Paragraph("Entrada: " + factura.getReserva().getFechaEntrada()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont));
        document.add(new Paragraph("Salida: " + factura.getReserva().getFechaSalida()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 1, 2, 2});

        table.addCell(new Phrase("Descripción", boldFont));
        table.addCell(new Phrase("Cant.", boldFont));
        table.addCell(new Phrase("Precio Unit.", boldFont));
        table.addCell(new Phrase("Total", boldFont));

        for (FacturaItem item : factura.getItems()) {
            table.addCell(new Phrase(item.getDescripcion(), normalFont));
            table.addCell(new Phrase(String.valueOf(item.getCantidad()), normalFont));
            table.addCell(new Phrase(String.format("$%.2f", item.getPrecioUnitario()), normalFont));
            table.addCell(new Phrase(String.format("$%.2f", item.getTotal()), normalFont));
        }

        document.add(table);
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Subtotal: $" + String.format("%.2f", factura.getSubtotal()), normalFont));
        document.add(new Paragraph("IVA (" + ivaPorcentaje + "%): $" + String.format("%.2f", factura.getIva()), normalFont));
        document.add(new Paragraph("TOTAL: $" + String.format("%.2f", factura.getTotal()), boldFont));
        document.add(new Paragraph(" "));

        String estadoPago = factura.getPagada() ? "PAGADA" : "PENDIENTE DE PAGO";
        Paragraph estadoPar = new Paragraph("Estado: " + estadoPago, headerFont);
        estadoPar.setAlignment(Element.ALIGN_RIGHT);
        document.add(estadoPar);

        document.close();
        return baos.toByteArray();
    }

    private String generarNumeroFactura() {
        LocalDate hoy = LocalDate.now();
        String prefix = "FAC-" + hoy.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        long count = facturaDao.findAll().stream()
                .filter(f -> f.getNumeroFactura().startsWith(prefix))
                .count();
        return prefix + String.format("%05d", count + 1);
    }
}
