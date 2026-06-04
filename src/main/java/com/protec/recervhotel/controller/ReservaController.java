package com.protec.recervhotel.controller;

import com.protec.recervhotel.dto.ReservaCreacionDTO;
import com.protec.recervhotel.dto.ReservaDTO;
import com.protec.recervhotel.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<ReservaDTO>> listar(
            @RequestParam(required = false) Long habitacionId) {
        if (habitacionId != null) {
            return ResponseEntity.ok(reservaService.listarPorHabitacion(habitacionId));
        }
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    @GetMapping("/estadisticas/ocupadas")
    public ResponseEntity<Long> contarOcupadasEnFecha(@RequestParam LocalDate fecha) {
        return ResponseEntity.ok(reservaService.contarOcupadasEnFecha(fecha));
    }

    @GetMapping("/estadisticas/ingresos-por-mes")
    public ResponseEntity<List<Map<String, Object>>> ingresosPorMes() {
        return ResponseEntity.ok(reservaService.ingresosPorMes());
    }

    @GetMapping("/estadisticas/ocupacion-por-mes")
    public ResponseEntity<List<Map<String, Object>>> ocupacionPorMes() {
        return ResponseEntity.ok(reservaService.ocupacionAgrupadaPorMes());
    }

    @GetMapping("/estadisticas/tendencia")
    public ResponseEntity<List<Map<String, Object>>> tendencia(@RequestParam LocalDate desde) {
        return ResponseEntity.ok(reservaService.tendenciaDesde(desde));
    }

    @GetMapping("/estadisticas/habitaciones-mas-reservadas")
    public ResponseEntity<List<Map<String, Object>>> habitacionesMasReservadas() {
        return ResponseEntity.ok(reservaService.habitacionesMasReservadas());
    }
}
