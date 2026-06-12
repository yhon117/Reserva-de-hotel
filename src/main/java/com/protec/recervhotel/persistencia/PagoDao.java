package com.protec.recervhotel.persistencia;

import com.protec.recervhotel.entities.Pago;

import java.util.List;
import java.util.Optional;

public interface PagoDao {
    void save(Pago pago);
    Optional<Pago> findById(Long id);
    List<Pago> findAll();
    List<Pago> findByReservaId(Long reservaId);
}
