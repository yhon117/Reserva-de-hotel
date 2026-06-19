package com.protec.recervhotel.controller;

import com.protec.recervhotel.dto.ReservaCreacionDTO;
import com.protec.recervhotel.dto.ReservaDTO;
import com.protec.recervhotel.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<ReservaDTO> crear(@Valid @RequestBody ReservaCreacionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crear(dto));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ReservaDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.cancelar(id));
    }

    @GetMapping("/mias")
    public ResponseEntity<List<ReservaDTO>> listarMias(Authentication auth) {
        return ResponseEntity.ok(reservaService.listarPorEmailUsuario(auth.getName()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @GetMapping
    public ResponseEntity<List<ReservaDTO>> listar(
            @RequestParam(required = false) Long habitacionId) {
        if (habitacionId != null) {
            return ResponseEntity.ok(reservaService.listarPorHabitacion(habitacionId));
        }
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @GetMapping("/estadisticas/ocupadas")
    public ResponseEntity<Long> contarOcupadasEnFecha(@RequestParam LocalDate fecha) {
        return ResponseEntity.ok(reservaService.contarOcupadasEnFecha(fecha));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @GetMapping("/estadisticas/ingresos-por-mes")
    public ResponseEntity<List<Map<String, Object>>> ingresosPorMes() {
        return ResponseEntity.ok(reservaService.ingresosPorMes());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @GetMapping("/estadisticas/ocupacion-por-mes")
    public ResponseEntity<List<Map<String, Object>>> ocupacionPorMes() {
        return ResponseEntity.ok(reservaService.ocupacionAgrupadaPorMes());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @GetMapping("/estadisticas/tendencia")
    public ResponseEntity<List<Map<String, Object>>> tendencia(@RequestParam LocalDate desde) {
        return ResponseEntity.ok(reservaService.tendenciaDesde(desde));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCIONISTA')")
    @GetMapping("/estadisticas/habitaciones-mas-reservadas")
    public ResponseEntity<List<Map<String, Object>>> habitacionesMasReservadas() {
        return ResponseEntity.ok(reservaService.habitacionesMasReservadas());
    }
}
