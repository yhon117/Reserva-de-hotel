package com.protec.recervhotel.persistencia;


import com.protec.recervhotel.entities.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioDao {

    void save(Usuario usuario);

    Optional<Usuario> findById(Long id);

    Optional<Usuario> findByEmail(String email);

    List<Usuario>findAll();

    void delete(Long id);

}
