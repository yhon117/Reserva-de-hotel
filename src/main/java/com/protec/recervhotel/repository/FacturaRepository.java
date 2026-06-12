package com.protec.recervhotel.repository;

import com.protec.recervhotel.entities.Factura;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FacturaRepository extends CrudRepository<Factura, Long> {
    Optional<Factura> findByReservaId(Long reservaId);
    List<Factura> findAllByOrderByFechaEmisionDesc();
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    boolean existsByReservaId(Long reservaId);
}
