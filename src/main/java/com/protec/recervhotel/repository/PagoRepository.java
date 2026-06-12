package com.protec.recervhotel.repository;

import com.protec.recervhotel.entities.Pago;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PagoRepository extends CrudRepository<Pago, Long> {
    List<Pago> findByReservaIdOrderByFechaPagoDesc(Long reservaId);
    List<Pago> findAllByOrderByFechaPagoDesc();
    Optional<Pago> findByCodigoTransaccion(String codigoTransaccion);
}
