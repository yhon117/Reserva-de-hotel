package com.protec.recervhotel.repository;

import com.protec.recervhotel.entitys.Habitacion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitacionRepository extends CrudRepository<Habitacion,Long> {
}
