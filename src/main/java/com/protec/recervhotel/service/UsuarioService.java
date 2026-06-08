package com.protec.recervhotel.service;

import com.protec.recervhotel.dto.LoginResponseDTO;
import com.protec.recervhotel.dto.UsuarioActualizacionDTO;
import com.protec.recervhotel.dto.UsuarioDTO;
import com.protec.recervhotel.dto.UsuarioLoginDTO;
import com.protec.recervhotel.dto.UsuarioRegistroDTO;
import com.protec.recervhotel.enums.Rol;
import com.protec.recervhotel.exception.BusinessException;
import com.protec.recervhotel.exception.ResourceNotFoundException;
import com.protec.recervhotel.mappers.UsuarioMapper;
import com.protec.recervhotel.persistencia.UsuarioDao;
import com.protec.recervhotel.entities.Usuario;
import com.protec.recervhotel.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioDao usuarioDao;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public UsuarioService(UsuarioDao usuarioDao, UsuarioMapper usuarioMapper,
                          PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.usuarioDao = usuarioDao;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public UsuarioDTO registrar(UsuarioRegistroDTO dto) {
        if (usuarioDao.findByEmail(dto.getEmail()).isPresent()) {
            throw new BusinessException("El email ya está registrado");
        }
        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(Rol.USER);
        usuarioDao.save(usuario);
        return usuarioMapper.toDto(usuario);
    }

    public LoginResponseDTO login(UsuarioLoginDTO dto) {
        Usuario usuario = usuarioDao.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BusinessException("Credenciales inválidas"));
        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new BusinessException("Credenciales inválidas");
        }
        String rolName = usuario.getRol() != null ? usuario.getRol().name() : "USER";
        String token = jwtTokenProvider.generateToken(usuario.getEmail(), rolName);
        return LoginResponseDTO.builder()
                .token(token)
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .rol(usuario.getRol())
                .build();
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

    @Transactional
    public UsuarioDTO crearPorAdmin(UsuarioRegistroDTO dto) {
        if (usuarioDao.findByEmail(dto.getEmail()).isPresent()) {
            throw new BusinessException("El email ya está registrado");
        }
        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(dto.getRol() != null ? dto.getRol() : Rol.USER);
        usuarioDao.save(usuario);
        return usuarioMapper.toDto(usuario);
    }

    @Transactional
    public UsuarioDTO actualizar(Long id, UsuarioActualizacionDTO dto) {
        Usuario usuario = usuarioDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        if (!usuario.getEmail().equals(dto.getEmail())
                && usuarioDao.findByEmail(dto.getEmail()).isPresent()) {
            throw new BusinessException("El email ya está registrado");
        }
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setTelefono(dto.getTelefono());
        if (dto.getRol() != null) {
            usuario.setRol(dto.getRol());
        }
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        usuarioDao.save(usuario);
        return usuarioMapper.toDto(usuario);
    }

    @Transactional
    public UsuarioDTO cambiarRol(Long id, Rol nuevoRol) {
        Usuario usuario = usuarioDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        usuario.setRol(nuevoRol);
        usuarioDao.save(usuario);
        return usuarioMapper.toDto(usuario);
    }
}
