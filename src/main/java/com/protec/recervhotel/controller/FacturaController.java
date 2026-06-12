package com.protec.recervhotel.controller;

import com.protec.recervhotel.dto.FacturaDTO;
import com.protec.recervhotel.dto.FacturaResumenDTO;
import com.protec.recervhotel.service.FacturaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
@PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.obtenerPorId(id));
    }

    @GetMapping("/por-reserva/{reservaId}")
    public ResponseEntity<FacturaDTO> obtenerPorReserva(@PathVariable Long reservaId) {
        return ResponseEntity.ok(facturaService.obtenerPorReservaId(reservaId));
    }

    @GetMapping
    public ResponseEntity<List<FacturaResumenDTO>> listarTodas() {
        return ResponseEntity.ok(facturaService.listarTodas());
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generarPdf(@PathVariable Long id) {
        byte[] pdf = facturaService.generarPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=factura-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
