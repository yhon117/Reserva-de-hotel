package com.protec.recervhotel.repository;

import com.protec.recervhotel.entitys.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario,Long> {


}
