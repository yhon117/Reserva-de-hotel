package com.protec.recervhotel.controller;

import com.protec.recervhotel.dto.HabitacionCreacionDTO;
import com.protec.recervhotel.dto.HabitacionDTO;
import com.protec.recervhotel.service.HabitacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionController {

    private final HabitacionService habitacionService;

    public HabitacionController(HabitacionService habitacionService) {
        this.habitacionService = habitacionService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<HabitacionDTO> crear(@Valid @RequestBody HabitacionCreacionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(habitacionService.crear(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitacionDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(habitacionService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<HabitacionDTO>> listarTodas() {
        return ResponseEntity.ok(habitacionService.listarTodas());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<HabitacionDTO> actualizar(@PathVariable Long id,
                                                    @Valid @RequestBody HabitacionCreacionDTO dto) {
        return ResponseEntity.ok(habitacionService.actualizar(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        habitacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<HabitacionDTO>> buscarDisponiblesPorTipo(@RequestParam String tipo) {
        return ResponseEntity.ok(habitacionService.buscarDisponiblesPorTipo(tipo));
    }

    @GetMapping("/disponibles/precio")
    public ResponseEntity<List<HabitacionDTO>> buscarDisponiblesPorPrecio(@RequestParam Double max) {
        return ResponseEntity.ok(habitacionService.buscarDisponiblesPorPrecioMaximo(max));
    }

    @GetMapping("/disponibles/capacidad")
    public ResponseEntity<List<HabitacionDTO>> buscarDisponiblesPorCapacidad(@RequestParam Integer personas) {
        return ResponseEntity.ok(habitacionService.buscarDisponiblesPorCapacidad(personas));
    }
}
