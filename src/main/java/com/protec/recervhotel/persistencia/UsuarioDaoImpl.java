package com.protec.recervhotel.persistencia;

import com.protec.recervhotel.entitys.Usuario;
import com.protec.recervhotel.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Component
public class UsuarioDaoImpl implements UsuarioDao{

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    public void save(Usuario usuario) {

        usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public List<Usuario> findAll() {
        return (List<Usuario>) usuarioRepository.findAll();
    }

    @Override
    public void delete(Long id) {

        usuarioRepository.deleteById(id);
    }
}
