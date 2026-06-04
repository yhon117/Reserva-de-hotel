package com.protec.recervhotel.persistencia;

import com.protec.recervhotel.entities.Usuario;
import com.protec.recervhotel.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
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
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
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
