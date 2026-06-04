package com.protec.recervhotel.controller;

import com.protec.recervhotel.dto.UsuarioDTO;
import com.protec.recervhotel.dto.UsuarioLoginDTO;
import com.protec.recervhotel.dto.UsuarioRegistroDTO;
import com.protec.recervhotel.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> registrar(@Valid @RequestBody UsuarioRegistroDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> login(@Valid @RequestBody UsuarioLoginDTO dto) {
        return ResponseEntity.ok(usuarioService.login(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
