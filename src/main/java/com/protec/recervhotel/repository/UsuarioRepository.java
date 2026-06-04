package com.protec.recervhotel.repository;

import com.protec.recervhotel.entities.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario,Long> {

    Optional<Usuario> findByEmail(String email);
}
