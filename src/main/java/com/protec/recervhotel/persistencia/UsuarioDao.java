package com.protec.recervhotel.persistencia;


import com.protec.recervhotel.entitys.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioDao {

    void save(Usuario usuario);

    Optional<Usuario> findById(Long id);

    List<Usuario>findAll();

    void delete(Long id);

}
