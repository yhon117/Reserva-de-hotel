package com.protec.recervhotel.controller;

import com.protec.recervhotel.dto.PagoCreacionDTO;
import com.protec.recervhotel.dto.PagoDTO;
import com.protec.recervhotel.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @PostMapping
    public ResponseEntity<PagoDTO> registrar(@Valid @RequestBody PagoCreacionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.registrarPago(dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @GetMapping("/{id}")
    public ResponseEntity<PagoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pagoService.obtenerPorId(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @GetMapping
    public ResponseEntity<List<PagoDTO>> listar(
            @RequestParam(required = false) Long reservaId) {
        if (reservaId != null) {
            return ResponseEntity.ok(pagoService.listarPorReserva(reservaId));
        }
        return ResponseEntity.ok(pagoService.listarTodos());
    }
}
