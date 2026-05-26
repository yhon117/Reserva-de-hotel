package com.protec.recervhotel.repository;

import com.protec.recervhotel.entitys.Reserva;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaReposistory extends CrudRepository<Reserva,Long> {

}
