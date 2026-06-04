package com.protec.recervhotel.service;

import com.protec.recervhotel.dto.UsuarioDTO;
import com.protec.recervhotel.dto.UsuarioLoginDTO;
import com.protec.recervhotel.dto.UsuarioRegistroDTO;
import com.protec.recervhotel.exception.BusinessException;
import com.protec.recervhotel.exception.ResourceNotFoundException;
import com.protec.recervhotel.mappers.UsuarioMapper;
import com.protec.recervhotel.persistencia.UsuarioDao;
import com.protec.recervhotel.entities.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioDao usuarioDao;
    private final UsuarioMapper usuarioMapper;

    public UsuarioService(UsuarioDao usuarioDao, UsuarioMapper usuarioMapper) {
        this.usuarioDao = usuarioDao;
        this.usuarioMapper = usuarioMapper;
    }

    @Transactional
    public UsuarioDTO registrar(UsuarioRegistroDTO dto) {
        if (usuarioDao.findByEmail(dto.getEmail()).isPresent()) {
            throw new BusinessException("El email ya está registrado");
        }
        Usuario usuario = usuarioMapper.toEntity(dto);
        usuarioDao.save(usuario);
        return usuarioMapper.toDto(usuario);
    }

    public UsuarioDTO login(UsuarioLoginDTO dto) {
        Usuario usuario = usuarioDao.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BusinessException("Credenciales inválidas"));
        if (!usuario.getPassword().equals(dto.getPassword())) {
            throw new BusinessException("Credenciales inválidas");
        }
        return usuarioMapper.toDto(usuario);
    }

    public UsuarioDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        return usuarioMapper.toDto(usuario);
    }

    public List<UsuarioDTO> listarTodos() {
        return usuarioMapper.toListDto(usuarioDao.findAll());
    }

    @Transactional
    public void eliminar(Long id) {
        if (usuarioDao.findById(id).isEmpty()) {
            throw new ResourceNotFoundException("Usuario", id);
        }
        usuarioDao.delete(id);
    }
}
