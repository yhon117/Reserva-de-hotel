package com.protec.recervhotel.persistencia;

import com.protec.recervhotel.entities.Factura;

import java.util.List;
import java.util.Optional;

public interface FacturaDao {
    void save(Factura factura);
    Optional<Factura> findById(Long id);
    List<Factura> findAll();
    Optional<Factura> findByReservaId(Long reservaId);
    boolean existsByReservaId(Long reservaId);
}
